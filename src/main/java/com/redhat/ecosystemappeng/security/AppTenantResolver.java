package com.redhat.ecosystemappeng.security;

import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkus.oidc.OidcRequestContext;
import io.quarkus.oidc.OidcTenantConfig;
import io.quarkus.oidc.TenantConfigResolver;
import io.quarkus.oidc.OidcTenantConfig.ApplicationType;
import io.quarkus.oidc.common.runtime.OidcCommonConfig.Tls.Verification;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;

@ApplicationScoped
public class AppTenantResolver implements TenantConfigResolver {

    private static final Pattern TENANT_REGEX = Pattern.compile("/app/([a-zA-Z0-9\\-_]+).*");
    private static final Set<String> TENANTS = Set.of("acme", "ajax");

    @Override
    public Uni<OidcTenantConfig> resolve(RoutingContext context, OidcRequestContext<OidcTenantConfig> requestContext) {
        Matcher matcher = TENANT_REGEX.matcher(context.request().path());

        if (matcher.matches()) {
            String tenant = matcher.group(1);
            if (TENANTS.contains(tenant)) {
                String keycloakUrl = ConfigProvider.getConfig().getValue("keycloak.url", String.class);

                OidcTenantConfig config = new OidcTenantConfig();
                config.setTenantId(tenant);
                config.setAuthServerUrl(keycloakUrl + "/realms/" + tenant);
                config.setClientId("multi-tenant-client");
                config.getCredentials()
                        .setSecret(ConfigProvider.getConfig().getValue("tenant." + tenant + ".secret", String.class));
                config.setApplicationType(ApplicationType.WEB_APP);
                config.tls.verification = Optional.of(Verification.NONE);
                config.logout.path = Optional.of("/logout");
                config.logout.postLogoutPath = Optional.of("/");
                return Uni.createFrom().item(config);
            }
        }
        // resolve to default tenant config
        return Uni.createFrom().nullItem();
    }
}
