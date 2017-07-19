package org.apereo.cas.configuration.model.support.mfa;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.configuration.model.core.util.EncryptionJwtSigningJwtCryptographyProperties;
import org.apereo.cas.configuration.model.support.jpa.AbstractJpaProperties;
import org.apereo.cas.configuration.model.support.mongo.AbstractMongoClientProperties;
import org.apereo.cas.configuration.support.AbstractConfigProperties;
import org.apereo.cas.configuration.support.Beans;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.io.Resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Configuration properties class for cas.mfa.
 *
 * @author Dmitriy Kopylenko
 * @since 5.0.0
 */
public class MultifactorAuthenticationProperties implements Serializable {
    private static final long serialVersionUID = 7416521468929733907L;

    /**
     * Attribute returned in the final CAS validation payload
     * that indicates the authentication context class satisified
     * in the event of a multifactor authentication attempt.
     */
    private String authenticationContextAttribute = "authnContextClass";
    /**
     * Defines the global failure mode for the entire deployment.
     * This is meant to be used a shortcut to define the policy globally
     * rather than per application. Applications registered with CAS can still
     * define a failure mode and override the global.
     */
    private String globalFailureMode = "CLOSED";
    /**
     * MFA can be triggered for a specific authentication request,
     * provided the initial request to the CAS /login endpoint contains a parameter that indicates the required MFA authentication flow.
     * The parameter name is configurable, but its value must match the authentication provider id of an available MFA provider.
     */
    private String requestParameter = "authn_method";

    /**
     * MFA can be triggered based on the results of a remote REST endpoint of your design.
     * If the endpoint is configured, CAS shall issue a POST, providing the principal and the service url.
     * The body of the response in the event of a successful 200 status code is
     * expected to be the MFA provider id which CAS should activate.
     */
    private String restEndpoint;

    /**
     * MFA can be triggered based on the results of a groovy script of your own design.
     * The outcome of the script should determine the MFA provider id that CAS should attempt to activate.
     */
    private Resource groovyScript;

    /**
     * This is a more generic variant of the @{link #globalPrincipalAttributeNameTriggers}.
     * It may be useful in cases where there
     * is more than one provider configured and available in the application runtime and
     * you need to design a strategy to dynamically decide on the provider that should be activated for the request.
     * The decision is handed off to a Predicate implementation that define in a Groovy script whose location is taught to CAS.
     */
    private Resource globalPrincipalAttributePredicate;
    /**
     * MFA can be triggered for all users/subjects carrying a specific attribute that matches one of the conditions below.
     * <ul>
     * <li>Trigger MFA based on a principal attribute(s) whose value(s) matches a regex pattern.
     * Note that this behavior is only applicable if there is only a single MFA provider configured,
     * since that would allow CAS to know what provider to next activate.</li>
     * <li>Trigger MFA based on a principal attribute(s) whose value(s) EXACTLY matches an MFA provider.
     * This option is more relevant if you have more than one provider configured or if you have the flexibility
     * of assigning provider ids to attributes as values.</li>
     * </ul>
     * Needless to say, the attributes need to have been resolved for the principal prior to this step.
     */
    private String globalPrincipalAttributeNameTriggers;
    /**
     * The regular expression that is cross matches against the principal attribute to determine
     * if the account is qualified for multifactor authentication.
     */
    private String globalPrincipalAttributeValueRegex;

    /**
     * MFA can be triggered for all users/subjects whose authentication event/metadata has resolved a specific attribute that
     * matches one of the below conditions:
     * <ul>
     * <li>Trigger MFA based on a authentication attribute(s) whose value(s) matches a regex pattern.
     * Note that this behavior is only applicable if there is only a single MFA provider configured,
     * since that would allow CAS to know what provider to next activate. </li>
     * <li>Trigger MFA based on a authentication attribute(s) whose value(s) EXACTLY matches an MFA provider.
     * This option is more relevant if you have more than one provider configured or if you have the
     * flexibility of assigning provider ids to attributes as values. </li>
     * </ul>
     * Needless to say, the attributes need to have been resolved for the authentication event prior to this step.
     * This trigger is generally useful when the underlying authentication engine signals
     * CAS to perform additional validation of credentials. This signal may be captured by CAS as
     * an attribute that is part of the authentication event metadata which can then trigger
     * additional multifactor authentication events.
     */
    private String globalAuthenticationAttributeNameTriggers;
    /**
     * The regular expression that is cross matches against the authentication attribute to determine
     * if the account is qualified for multifactor authentication.
     */
    private String globalAuthenticationAttributeValueRegex;

    /**
     * Content-type that is expected to be specified by non-web clients such as curl, etc in the
     * event that the provider supports variations of non-browser based MFA.
     */
    private String contentType = "application/cas";
    /**
     * MFA can be triggered for all applications and users regardless of individual settings.
     * This setting holds the value of an MFA provider that shall be activated for all requests,
     * regardless.
     */
    private String globalProviderId;

    /**
     * MFA can be triggered by Grouper groups to which the authenticated principal is assigned.
     * Groups are collected by CAS and then cross-checked against all available/configured MFA providers.
     * The group’s comparing factor MUST be defined in CAS to activate this behavior and
     * it can be based on the group’s name, display name,
     * etc where a successful match against a provider id shall activate the chosen MFA provider.
     */
    private String grouperGroupField;
    /**
     * In the event that multiple multifactor authentication providers are determined for a multifactor authentication transaction,
     * by default CAS will attempt to sort the collection of providers based on their rank and
     * will pick one with the highest priority. This use case may arise if multiple triggers
     * are defined where each decides on a different multifactor authentication provider, or
     * the same provider instance is configured multiple times with many instances.
     * Provider selection may also be carried out using Groovy scripting strategies more dynamically.
     * The following example should serve as an outline of how to select multifactor providers based on a Groovy script.
     */
    private Resource providerSelectorGroovyScript;

    /**
     * Activate and configure a multifactor authentication provider via U2F FIDO.
     */
    private U2F u2f = new U2F();
    /**
     * Activate and configure a multifactor authentication provider via Microsoft Azure.
     */
    private Azure azure = new Azure();
    /**
     * Activate and configure a multifactor authentication with the capability to trust and remember devices.
     */
    private Trusted trusted = new Trusted();
    /**
     * Activate and configure a multifactor authentication provider via YubiKey.
     */
    private YubiKey yubikey = new YubiKey();
    /**
     * Activate and configure a multifactor authentication provider via RADIUS.
     */
    private Radius radius = new Radius();
    /**
     * Activate and configure a multifactor authentication provider via Google Authenticator.
     */
    private GAuth gauth = new GAuth();
    /**
     * Activate and configure a multifactor authentication provider via Duo Security.
     */
    private List<Duo> duo = new ArrayList<>();
    /**
     * Activate and configure a multifactor authentication provider via Authy.
     */
    private Authy authy = new Authy();
    /**
     * Activate and configure a multifactor authentication provider via Swivel.
     */
    private Swivel swivel = new Swivel();

    public Resource getGlobalPrincipalAttributePredicate() {
        return globalPrincipalAttributePredicate;
    }

    public void setGlobalPrincipalAttributePredicate(final Resource globalPrincipalAttributePredicate) {
        this.globalPrincipalAttributePredicate = globalPrincipalAttributePredicate;
    }

    public Resource getProviderSelectorGroovyScript() {
        return providerSelectorGroovyScript;
    }

    public void setProviderSelectorGroovyScript(final Resource providerSelectorGroovyScript) {
        this.providerSelectorGroovyScript = providerSelectorGroovyScript;
    }

    public Resource getGroovyScript() {
        return groovyScript;
    }

    public void setGroovyScript(final Resource groovyScript) {
        this.groovyScript = groovyScript;
    }

    public Swivel getSwivel() {
        return swivel;
    }

    public void setSwivel(final Swivel swivel) {
        this.swivel = swivel;
    }

    public U2F getU2f() {
        return u2f;
    }

    public void setU2f(final U2F u2f) {
        this.u2f = u2f;
    }

    public Azure getAzure() {
        return azure;
    }

    public void setAzure(final Azure azure) {
        this.azure = azure;
    }

    public Trusted getTrusted() {
        return trusted;
    }

    public void setTrusted(final Trusted trusted) {
        this.trusted = trusted;
    }

    public Authy getAuthy() {
        return authy;
    }

    public void setAuthy(final Authy authy) {
        this.authy = authy;
    }

    public String getRestEndpoint() {
        return restEndpoint;
    }

    public void setRestEndpoint(final String restEndpoint) {
        this.restEndpoint = restEndpoint;
    }

    public String getRequestParameter() {
        return requestParameter;
    }

    public void setRequestParameter(final String requestParameter) {
        this.requestParameter = requestParameter;
    }

    public String getGlobalAuthenticationAttributeNameTriggers() {
        return globalAuthenticationAttributeNameTriggers;
    }

    public void setGlobalAuthenticationAttributeNameTriggers(final String globalAuthenticationAttributeNameTriggers) {
        this.globalAuthenticationAttributeNameTriggers = globalAuthenticationAttributeNameTriggers;
    }

    public String getGlobalAuthenticationAttributeValueRegex() {
        return globalAuthenticationAttributeValueRegex;
    }

    public void setGlobalAuthenticationAttributeValueRegex(final String globalAuthenticationAttributeValueRegex) {
        this.globalAuthenticationAttributeValueRegex = globalAuthenticationAttributeValueRegex;
    }

    public String getGlobalPrincipalAttributeValueRegex() {
        return globalPrincipalAttributeValueRegex;
    }

    public void setGlobalPrincipalAttributeValueRegex(final String globalPrincipalAttributeValueRegex) {
        this.globalPrincipalAttributeValueRegex = globalPrincipalAttributeValueRegex;
    }

    public String getGlobalPrincipalAttributeNameTriggers() {
        return globalPrincipalAttributeNameTriggers;
    }

    public void setGlobalPrincipalAttributeNameTriggers(final String globalPrincipalAttributeNameTriggers) {
        this.globalPrincipalAttributeNameTriggers = globalPrincipalAttributeNameTriggers;
    }

    public String getGrouperGroupField() {
        return grouperGroupField;
    }

    public void setGrouperGroupField(final String grouperGroupField) {
        this.grouperGroupField = grouperGroupField;
    }

    public List<Duo> getDuo() {
        return duo;
    }

    public void setDuo(final List<Duo> duo) {
        this.duo = duo;
    }

    public GAuth getGauth() {
        return gauth;
    }

    public void setGauth(final GAuth gauth) {
        this.gauth = gauth;
    }

    public Radius getRadius() {
        return radius;
    }

    public void setRadius(final Radius radius) {
        this.radius = radius;
    }

    public String getGlobalFailureMode() {
        return globalFailureMode;
    }

    public void setGlobalFailureMode(final String globalFailureMode) {
        this.globalFailureMode = globalFailureMode;
    }

    public String getAuthenticationContextAttribute() {
        return authenticationContextAttribute;
    }

    public void setAuthenticationContextAttribute(final String authenticationContextAttribute) {
        this.authenticationContextAttribute = authenticationContextAttribute;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    public String getGlobalProviderId() {
        return globalProviderId;
    }

    public void setGlobalProviderId(final String globalProviderId) {
        this.globalProviderId = globalProviderId;
    }

    public YubiKey getYubikey() {
        return yubikey;
    }

    public void setYubikey(final YubiKey yubikey) {
        this.yubikey = yubikey;
    }

    public abstract static class BaseProvider implements Serializable {
        private static final long serialVersionUID = -2690281104343633871L;
        private int rank;
        private String id;
        private Bypass bypass = new Bypass();
        private String name;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public Bypass getBypass() {
            return bypass;
        }

        public void setBypass(final Bypass bypass) {
            this.bypass = bypass;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(final int rank) {
            this.rank = rank;
        }

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public static class Bypass implements Serializable {
            private static final long serialVersionUID = -9181362378365850397L;
            private String principalAttributeName;
            private String principalAttributeValue;
            private String authenticationAttributeName;
            private String authenticationAttributeValue;
            private String authenticationHandlerName;
            private String authenticationMethodName;
            private String credentialClassType;

            public String getCredentialClassType() {
                return credentialClassType;
            }

            public void setCredentialClassType(final String credentialClassType) {
                this.credentialClassType = credentialClassType;
            }

            public String getAuthenticationAttributeName() {
                return authenticationAttributeName;
            }

            public void setAuthenticationAttributeName(final String authenticationAttributeName) {
                this.authenticationAttributeName = authenticationAttributeName;
            }

            public String getAuthenticationAttributeValue() {
                return authenticationAttributeValue;
            }

            public void setAuthenticationAttributeValue(final String authenticationAttributeValue) {
                this.authenticationAttributeValue = authenticationAttributeValue;
            }

            public String getPrincipalAttributeName() {
                return principalAttributeName;
            }

            public void setPrincipalAttributeName(final String principalAttributeName) {
                this.principalAttributeName = principalAttributeName;
            }

            public String getPrincipalAttributeValue() {
                return principalAttributeValue;
            }

            public void setPrincipalAttributeValue(final String principalAttributeValue) {
                this.principalAttributeValue = principalAttributeValue;
            }

            public String getAuthenticationHandlerName() {
                return authenticationHandlerName;
            }

            public void setAuthenticationHandlerName(final String authenticationHandlerName) {
                this.authenticationHandlerName = authenticationHandlerName;
            }

            public String getAuthenticationMethodName() {
                return authenticationMethodName;
            }

            public void setAuthenticationMethodName(final String authenticationMethodName) {
                this.authenticationMethodName = authenticationMethodName;
            }
        }
    }

    public static class U2F extends BaseProvider {
        private static final long serialVersionUID = 6151350313777066398L;

        private Memory memory = new Memory();
        private Jpa jpa = new Jpa();

        private long expireRegistrations = 30;
        private TimeUnit expireRegistrationsTimeUnit = TimeUnit.SECONDS;

        private long expireDevices = 30;
        private TimeUnit expireDevicesTimeUnit = TimeUnit.DAYS;

        private Json json = new Json();
        private Cleaner cleaner = new Cleaner();

        public U2F() {
            setId("mfa-u2f");
        }

        public Cleaner getCleaner() {
            return cleaner;
        }

        public void setCleaner(final Cleaner cleaner) {
            this.cleaner = cleaner;
        }

        public Json getJson() {
            return json;
        }

        public void setJson(final Json json) {
            this.json = json;
        }

        public long getExpireRegistrations() {
            return expireRegistrations;
        }

        public void setExpireRegistrations(final long expireRegistrations) {
            this.expireRegistrations = expireRegistrations;
        }

        public TimeUnit getExpireRegistrationsTimeUnit() {
            return expireRegistrationsTimeUnit;
        }

        public void setExpireRegistrationsTimeUnit(final TimeUnit expireRegistrationsTimeUnit) {
            this.expireRegistrationsTimeUnit = expireRegistrationsTimeUnit;
        }

        public long getExpireDevices() {
            return expireDevices;
        }

        public void setExpireDevices(final long expireDevices) {
            this.expireDevices = expireDevices;
        }

        public TimeUnit getExpireDevicesTimeUnit() {
            return expireDevicesTimeUnit;
        }

        public void setExpireDevicesTimeUnit(final TimeUnit expireDevicesTimeUnit) {
            this.expireDevicesTimeUnit = expireDevicesTimeUnit;
        }

        public static class Json extends AbstractConfigProperties {
            private static final long serialVersionUID = -6883660787308509919L;
        }

        public Jpa getJpa() {
            return jpa;
        }

        public void setJpa(final Jpa jpa) {
            this.jpa = jpa;
        }

        public Memory getMemory() {
            return memory;
        }

        public void setMemory(final Memory memory) {
            this.memory = memory;
        }

        public static class Memory implements Serializable {

            private static final long serialVersionUID = 771866433203773277L;
        }

        public static class Jpa extends AbstractJpaProperties {
            private static final long serialVersionUID = -4334840263678287815L;
        }

        public static class Cleaner {
            private boolean enabled = true;
            private String startDelay = "PT10S";
            private String repeatInterval = "PT1M";

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(final boolean enabled) {
                this.enabled = enabled;
            }

            public long getStartDelay() {
                return Beans.newDuration(startDelay).toMillis();
            }

            public void setStartDelay(final String startDelay) {
                this.startDelay = startDelay;
            }

            public long getRepeatInterval() {
                return Beans.newDuration(repeatInterval).toMillis();
            }

            public void setRepeatInterval(final String repeatInterval) {
                this.repeatInterval = repeatInterval;
            }
        }
    }

    public static class YubiKey extends BaseProvider {
        private static final long serialVersionUID = 9138057706201201089L;
        private Integer clientId;
        private String secretKey = StringUtils.EMPTY;

        private Resource jsonFile;
        private Map<String, String> allowedDevices;

        private List<String> apiUrls = new ArrayList<>();
        private boolean trustedDeviceEnabled;

        private Jpa jpa = new Jpa();
        private Mongodb mongodb = new Mongodb();

        public YubiKey() {
            setId("mfa-yubikey");
        }

        public boolean isTrustedDeviceEnabled() {
            return trustedDeviceEnabled;
        }

        public void setTrustedDeviceEnabled(final boolean trustedDeviceEnabled) {
            this.trustedDeviceEnabled = trustedDeviceEnabled;
        }

        public Integer getClientId() {
            return clientId;
        }

        public void setClientId(final Integer clientId) {
            this.clientId = clientId;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(final String secretKey) {
            this.secretKey = secretKey;
        }

        public List<String> getApiUrls() {
            return apiUrls;
        }

        public void setApiUrls(final List<String> apiUrls) {
            this.apiUrls = apiUrls;
        }

        public Resource getJsonFile() {
            return jsonFile;
        }

        public void setJsonFile(final Resource jsonFile) {
            this.jsonFile = jsonFile;
        }

        public Map<String, String> getAllowedDevices() {
            return allowedDevices;
        }

        public void setAllowedDevices(final Map<String, String> allowedDevices) {
            this.allowedDevices = allowedDevices;
        }

        public Jpa getJpa() {
            return jpa;
        }

        public void setJpa(final Jpa jpa) {
            this.jpa = jpa;
        }

        public Mongodb getMongodb() {
            return mongodb;
        }

        public void setMongodb(final Mongodb mongodb) {
            this.mongodb = mongodb;
        }

        public static class Jpa extends AbstractJpaProperties {
            private static final long serialVersionUID = -4420099402220880361L;
        }

        public static class Mongodb extends AbstractMongoClientProperties {
            private static final long serialVersionUID = 6876845341227039713L;

            public Mongodb() {
                setCollection("MongoDbYubiKeyRepository");
            }
        }
    }

    public static class Radius extends BaseProvider {
        private static final long serialVersionUID = 7021301814775348087L;
        private boolean failoverOnException;
        private boolean failoverOnAuthenticationFailure;

        private Server server = new Server();
        private Client client = new Client();

        private boolean trustedDeviceEnabled;


        public Radius() {
            setId("mfa-radius");
        }

        public boolean isTrustedDeviceEnabled() {
            return trustedDeviceEnabled;
        }

        public void setTrustedDeviceEnabled(final boolean trustedDeviceEnabled) {
            this.trustedDeviceEnabled = trustedDeviceEnabled;
        }

        public boolean isFailoverOnException() {
            return failoverOnException;
        }

        public void setFailoverOnException(final boolean failoverOnException) {
            this.failoverOnException = failoverOnException;
        }

        public boolean isFailoverOnAuthenticationFailure() {
            return failoverOnAuthenticationFailure;
        }

        public void setFailoverOnAuthenticationFailure(final boolean failoverOnAuthenticationFailure) {
            this.failoverOnAuthenticationFailure = failoverOnAuthenticationFailure;
        }

        public Server getServer() {
            return server;
        }

        public void setServer(final Server server) {
            this.server = server;
        }

        public Client getClient() {
            return client;
        }

        public void setClient(final Client client) {
            this.client = client;
        }

        public static class Server implements Serializable {
            private static final long serialVersionUID = -3911282132573730184L;
            private String protocol = "EAP_MSCHAPv2";
            private int retries = 3;
            private String nasIdentifier;
            private long nasPort = -1;
            private long nasPortId = -1;
            private long nasRealPort = -1;
            private int nasPortType = -1;
            private String nasIpAddress;
            private String nasIpv6Address;

            public String getProtocol() {
                return protocol;
            }

            public void setProtocol(final String protocol) {
                this.protocol = protocol;
            }

            public int getRetries() {
                return retries;
            }

            public void setRetries(final int retries) {
                this.retries = retries;
            }

            public String getNasIdentifier() {
                return nasIdentifier;
            }

            public void setNasIdentifier(final String nasIdentifier) {
                this.nasIdentifier = nasIdentifier;
            }

            public long getNasPort() {
                return nasPort;
            }

            public void setNasPort(final long nasPort) {
                this.nasPort = nasPort;
            }

            public long getNasPortId() {
                return nasPortId;
            }

            public void setNasPortId(final long nasPortId) {
                this.nasPortId = nasPortId;
            }

            public long getNasRealPort() {
                return nasRealPort;
            }

            public void setNasRealPort(final long nasRealPort) {
                this.nasRealPort = nasRealPort;
            }

            public int getNasPortType() {
                return nasPortType;
            }

            public void setNasPortType(final int nasPortType) {
                this.nasPortType = nasPortType;
            }

            public String getNasIpAddress() {
                return nasIpAddress;
            }

            public void setNasIpAddress(final String nasIpAddress) {
                this.nasIpAddress = nasIpAddress;
            }

            public String getNasIpv6Address() {
                return nasIpv6Address;
            }

            public void setNasIpv6Address(final String nasIpv6Address) {
                this.nasIpv6Address = nasIpv6Address;
            }


        }

        public static class Client implements Serializable {
            private static final long serialVersionUID = -7961769318651312854L;
            private String inetAddress = "localhost";
            private String sharedSecret = "N0Sh@ar3d$ecReT";
            private int socketTimeout;
            private int authenticationPort = 1812;
            private int accountingPort = 1813;

            public String getSharedSecret() {
                return sharedSecret;
            }

            public void setSharedSecret(final String sharedSecret) {
                this.sharedSecret = sharedSecret;
            }

            public int getSocketTimeout() {
                return socketTimeout;
            }

            public void setSocketTimeout(final int socketTimeout) {
                this.socketTimeout = socketTimeout;
            }

            public int getAuthenticationPort() {
                return authenticationPort;
            }

            public void setAuthenticationPort(final int authenticationPort) {
                this.authenticationPort = authenticationPort;
            }

            public int getAccountingPort() {
                return accountingPort;
            }

            public void setAccountingPort(final int accountingPort) {
                this.accountingPort = accountingPort;
            }

            public String getInetAddress() {
                return inetAddress;
            }

            public void setInetAddress(final String inetAddress) {
                this.inetAddress = inetAddress;
            }
        }
    }

    public static class Duo extends BaseProvider {
        private static final long serialVersionUID = -4445375354167880807L;
        private String duoIntegrationKey;
        private String duoSecretKey;
        private String duoApplicationKey;
        private String duoApiHost;

        private boolean trustedDeviceEnabled;

        public Duo() {
            setId("mfa-duo");
        }

        public boolean isTrustedDeviceEnabled() {
            return trustedDeviceEnabled;
        }

        public void setTrustedDeviceEnabled(final boolean trustedDeviceEnabled) {
            this.trustedDeviceEnabled = trustedDeviceEnabled;
        }

        public String getDuoIntegrationKey() {
            return duoIntegrationKey;
        }

        public void setDuoIntegrationKey(final String duoIntegrationKey) {
            this.duoIntegrationKey = duoIntegrationKey;
        }

        public String getDuoSecretKey() {
            return duoSecretKey;
        }

        public void setDuoSecretKey(final String duoSecretKey) {
            this.duoSecretKey = duoSecretKey;
        }

        public String getDuoApplicationKey() {
            return duoApplicationKey;
        }

        public void setDuoApplicationKey(final String duoApplicationKey) {
            this.duoApplicationKey = duoApplicationKey;
        }

        public String getDuoApiHost() {
            return duoApiHost;
        }

        public void setDuoApiHost(final String duoApiHost) {
            this.duoApiHost = duoApiHost;
        }
    }

    public static class Authy extends BaseProvider {
        private static final long serialVersionUID = -3746749663459157641L;
        private String apiKey;
        private String apiUrl;
        private String phoneAttribute = "phone";
        private String mailAttribute = "mail";
        private String countryCode = "1";
        private boolean forceVerification = true;
        private boolean trustedDeviceEnabled;

        public Authy() {
            setId("mfa-authy");
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(final String countryCode) {
            this.countryCode = countryCode;
        }

        public boolean isTrustedDeviceEnabled() {
            return trustedDeviceEnabled;
        }

        public void setTrustedDeviceEnabled(final boolean trustedDeviceEnabled) {
            this.trustedDeviceEnabled = trustedDeviceEnabled;
        }

        public String getPhoneAttribute() {
            return phoneAttribute;
        }

        public void setPhoneAttribute(final String phoneAttribute) {
            this.phoneAttribute = phoneAttribute;
        }

        public String getMailAttribute() {
            return mailAttribute;
        }

        public void setMailAttribute(final String mailAttribute) {
            this.mailAttribute = mailAttribute;
        }

        public boolean isForceVerification() {
            return forceVerification;
        }

        public void setForceVerification(final boolean forceVerification) {
            this.forceVerification = forceVerification;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(final String apiKey) {
            this.apiKey = apiKey;
        }

        public String getApiUrl() {
            return apiUrl;
        }

        public void setApiUrl(final String apiUrl) {
            this.apiUrl = apiUrl;
        }
    }

    public static class Trusted extends BaseProvider {
        private static final long serialVersionUID = 1505013239016790473L;
        private String authenticationContextAttribute = "isFromTrustedMultifactorAuthentication";

        private boolean deviceRegistrationEnabled = true;
        private long expiration = 30;
        private TimeUnit timeUnit = TimeUnit.DAYS;
        private Rest rest = new Rest();
        private Jpa jpa = new Jpa();
        private Cleaner cleaner = new Cleaner();
        private Mongodb mongodb = new Mongodb();

        @NestedConfigurationProperty
        private EncryptionJwtSigningJwtCryptographyProperties crypto = new EncryptionJwtSigningJwtCryptographyProperties();


        public EncryptionJwtSigningJwtCryptographyProperties getCrypto() {
            return crypto;
        }

        public void setCrypto(final EncryptionJwtSigningJwtCryptographyProperties crypto) {
            this.crypto = crypto;
        }

        public Rest getRest() {
            return rest;
        }

        public void setRest(final Rest rest) {
            this.rest = rest;
        }

        public Mongodb getMongodb() {
            return mongodb;
        }

        public void setMongodb(final Mongodb mongodb) {
            this.mongodb = mongodb;
        }

        public Jpa getJpa() {
            return jpa;
        }

        public void setJpa(final Jpa jpa) {
            this.jpa = jpa;
        }

        public long getExpiration() {
            return expiration;
        }

        public void setExpiration(final long expiration) {
            this.expiration = expiration;
        }

        public TimeUnit getTimeUnit() {
            return timeUnit;
        }

        public void setTimeUnit(final TimeUnit timeUnit) {
            this.timeUnit = timeUnit;
        }

        public String getAuthenticationContextAttribute() {
            return authenticationContextAttribute;
        }

        public void setAuthenticationContextAttribute(final String authenticationContextAttribute) {
            this.authenticationContextAttribute = authenticationContextAttribute;
        }

        public boolean isDeviceRegistrationEnabled() {
            return deviceRegistrationEnabled;
        }

        public void setDeviceRegistrationEnabled(final boolean deviceRegistrationEnabled) {
            this.deviceRegistrationEnabled = deviceRegistrationEnabled;
        }

        public Cleaner getCleaner() {
            return cleaner;
        }

        public void setCleaner(final Cleaner cleaner) {
            this.cleaner = cleaner;
        }

        public static class Rest implements Serializable {
            private static final long serialVersionUID = 3659099897056632608L;
            private String endpoint;

            public String getEndpoint() {
                return endpoint;
            }

            public void setEndpoint(final String endpoint) {
                this.endpoint = endpoint;
            }
        }

        public static class Jpa extends AbstractJpaProperties {
            private static final long serialVersionUID = -8329950619696176349L;
        }

        public static class Mongodb extends AbstractMongoClientProperties {
            private static final long serialVersionUID = 4940497540189318943L;

            public Mongodb() {
                setCollection("MongoDbCasTrustedAuthnMfaRepository");
            }
        }

        public static class Cleaner {
            private boolean enabled = true;
            private String startDelay = "PT15S";

            private String repeatInterval = "PT2M";

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(final boolean enabled) {
                this.enabled = enabled;
            }

            public long getStartDelay() {
                return Beans.newDuration(startDelay).toMillis();
            }

            public void setStartDelay(final String startDelay) {
                this.startDelay = startDelay;
            }

            public long getRepeatInterval() {
                return Beans.newDuration(repeatInterval).toMillis();
            }

            public void setRepeatInterval(final String repeatInterval) {
                this.repeatInterval = repeatInterval;
            }
        }
    }

    public static class Azure extends BaseProvider {
        private static final long serialVersionUID = 6726032660671158922L;

        /**
         * The enum Authentication modes.
         */
        public enum AuthenticationModes {
            /**
             * Ask the user to only press the pound sign.
             */
            POUND,
            /**
             * Ask the user to enter pin code shown on the screen.
             */
            PIN
        }

        private String phoneAttributeName = "phone";
        private String configDir;
        private String privateKeyPassword;
        private AuthenticationModes mode = AuthenticationModes.POUND;
        private boolean allowInternationalCalls;

        public Azure() {
            setId("mfa-azure");
        }

        public String getPhoneAttributeName() {
            return phoneAttributeName;
        }

        public void setPhoneAttributeName(final String phoneAttributeName) {
            this.phoneAttributeName = phoneAttributeName;
        }

        public AuthenticationModes getMode() {
            return mode;
        }

        public void setMode(final AuthenticationModes mode) {
            this.mode = mode;
        }

        public boolean isAllowInternationalCalls() {
            return allowInternationalCalls;
        }

        public void setAllowInternationalCalls(final boolean allowInternationalCalls) {
            this.allowInternationalCalls = allowInternationalCalls;
        }

        public String getConfigDir() {
            return configDir;
        }

        public void setConfigDir(final String configDir) {
            this.configDir = configDir;
        }

        public String getPrivateKeyPassword() {
            return privateKeyPassword;
        }

        public void setPrivateKeyPassword(final String privateKeyPassword) {
            this.privateKeyPassword = privateKeyPassword;
        }
    }

    public static class GAuth extends BaseProvider {
        private static final long serialVersionUID = -7401748853833491119L;
        private String issuer = "CASIssuer";
        private String label = "CASLabel";

        private int codeDigits = 6;
        private long timeStepSize = 30;
        private int windowSize = 3;

        private Mongodb mongodb = new Mongodb();
        private Jpa jpa = new Jpa();
        private Json json = new Json();
        private Rest rest = new Rest();

        private Cleaner cleaner = new Cleaner();

        public GAuth() {
            setId("mfa-gauth");
        }

        public Rest getRest() {
            return rest;
        }

        public void setRest(final Rest rest) {
            this.rest = rest;
        }

        public Cleaner getCleaner() {
            return cleaner;
        }

        public void setCleaner(final Cleaner cleaner) {
            this.cleaner = cleaner;
        }

        public Json getJson() {
            return json;
        }

        public void setJson(final Json json) {
            this.json = json;
        }

        public Mongodb getMongodb() {
            return mongodb;
        }

        public void setMongodb(final Mongodb mongodb) {
            this.mongodb = mongodb;
        }

        public Jpa getJpa() {
            return jpa;
        }

        public void setJpa(final Jpa jpa) {
            this.jpa = jpa;
        }

        public int getCodeDigits() {
            return codeDigits;
        }

        public void setCodeDigits(final int codeDigits) {
            this.codeDigits = codeDigits;
        }

        public long getTimeStepSize() {
            return timeStepSize;
        }

        public void setTimeStepSize(final long timeStepSize) {
            this.timeStepSize = timeStepSize;
        }

        public int getWindowSize() {
            return windowSize;
        }

        public void setWindowSize(final int windowSize) {
            this.windowSize = windowSize;
        }

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(final String issuer) {
            this.issuer = issuer;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(final String label) {
            this.label = label;
        }

        public static class Json extends AbstractConfigProperties {
            private static final long serialVersionUID = 4303355159388663888L;
        }

        public static class Rest implements Serializable {
            private static final long serialVersionUID = 4518622579150572559L;
            private String endpointUrl;

            public String getEndpointUrl() {
                return endpointUrl;
            }

            public void setEndpointUrl(final String endpointUrl) {
                this.endpointUrl = endpointUrl;
            }
        }

        public static class Mongodb extends AbstractMongoClientProperties {
            private static final long serialVersionUID = -200556119517414696L;
            private String tokenCollection;

            public Mongodb() {
                setCollection("MongoDbGoogleAuthenticatorRepository");
                setTokenCollection("MongoDbGoogleAuthenticatorTokenRepository");
            }

            public String getTokenCollection() {
                return tokenCollection;
            }

            public void setTokenCollection(final String tokenCollection) {
                this.tokenCollection = tokenCollection;
            }
        }

        public static class Jpa implements Serializable {
            private static final long serialVersionUID = -2689797889546802618L;
            private Database database = new Database();

            public Database getDatabase() {
                return database;
            }

            public void setDatabase(final Database database) {
                this.database = database;
            }

            public static class Database extends AbstractJpaProperties {
                private static final long serialVersionUID = -7446381055350251885L;

                public Database() {
                    super.setUrl("jdbc:hsqldb:mem:cas-gauth");
                }
            }
        }

        public static class Cleaner implements Serializable {
            private static final long serialVersionUID = -6036042153454544990L;
            private boolean enabled = true;
            private String startDelay = "PT1M";
            private String repeatInterval = "PT1M";

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(final boolean enabled) {
                this.enabled = enabled;
            }

            public String getStartDelay() {
                return startDelay;
            }

            public void setStartDelay(final String startDelay) {
                this.startDelay = startDelay;
            }

            public String getRepeatInterval() {
                return repeatInterval;
            }

            public void setRepeatInterval(final String repeatInterval) {
                this.repeatInterval = repeatInterval;
            }
        }
    }

    public static class Swivel extends BaseProvider {
        private static final long serialVersionUID = -7409451053833491119L;

        private String swivelTuringImageUrl;
        private String swivelUrl;
        private String sharedSecret;
        private boolean ignoreSslErrors;

        public Swivel() {
            setId("mfa-swivel");
        }

        public String getSwivelTuringImageUrl() {
            return swivelTuringImageUrl;
        }

        public void setSwivelTuringImageUrl(final String swivelTuringImageUrl) {
            this.swivelTuringImageUrl = swivelTuringImageUrl;
        }

        public String getSwivelUrl() {
            return swivelUrl;
        }

        public void setSwivelUrl(final String swivelUrl) {
            this.swivelUrl = swivelUrl;
        }

        public String getSharedSecret() {
            return sharedSecret;
        }

        public void setSharedSecret(final String sharedSecret) {
            this.sharedSecret = sharedSecret;
        }

        public boolean isIgnoreSslErrors() {
            return ignoreSslErrors;
        }

        public void setIgnoreSslErrors(final boolean ignoreSslErrors) {
            this.ignoreSslErrors = ignoreSslErrors;
        }
    }
}
