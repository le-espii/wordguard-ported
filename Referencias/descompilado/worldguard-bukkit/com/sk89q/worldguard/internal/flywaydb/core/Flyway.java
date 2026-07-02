/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationInfoService;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationVersion;
import com.sk89q.worldguard.internal.flywaydb.core.api.callback.FlywayCallback;
import com.sk89q.worldguard.internal.flywaydb.core.api.resolver.MigrationResolver;
import com.sk89q.worldguard.internal.flywaydb.core.internal.command.DbClean;
import com.sk89q.worldguard.internal.flywaydb.core.internal.command.DbInit;
import com.sk89q.worldguard.internal.flywaydb.core.internal.command.DbMigrate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.command.DbRepair;
import com.sk89q.worldguard.internal.flywaydb.core.internal.command.DbSchemas;
import com.sk89q.worldguard.internal.flywaydb.core.internal.command.DbValidate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupportFactory;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.info.MigrationInfoServiceImpl;
import com.sk89q.worldguard.internal.flywaydb.core.internal.metadatatable.MetaDataTable;
import com.sk89q.worldguard.internal.flywaydb.core.internal.metadatatable.MetaDataTableImpl;
import com.sk89q.worldguard.internal.flywaydb.core.internal.resolver.CompositeMigrationResolver;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.ClassUtils;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.Locations;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.PlaceholderReplacer;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StringUtils;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc.DriverDataSource;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc.JdbcUtils;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;

public class Flyway {
    private static final Log LOG = LogFactory.getLog(Flyway.class);
    private static final String PLACEHOLDERS_PROPERTY_PREFIX = "flyway.placeholders.";
    private Locations locations = new Locations("db/migration");
    private String encoding = "UTF-8";
    private String[] schemaNames = new String[0];
    private String table = "schema_version";
    private MigrationVersion target = MigrationVersion.LATEST;
    private Map<String, String> placeholders = new HashMap<String, String>();
    private String placeholderPrefix = "${";
    private String placeholderSuffix = "}";
    private String sqlMigrationPrefix = "V";
    private String sqlMigrationSeparator = "__";
    private String sqlMigrationSuffix = ".sql";
    private boolean ignoreFailedFutureMigration;
    private boolean validateOnMigrate = true;
    private boolean cleanOnValidationError;
    private MigrationVersion initVersion = MigrationVersion.fromVersion("1");
    private String initDescription = "<< Flyway Init >>";
    private boolean initOnMigrate;
    private boolean outOfOrder;
    private FlywayCallback[] callbacks = new FlywayCallback[0];
    private MigrationResolver[] resolvers = new MigrationResolver[0];
    private boolean createdDataSource;
    private DataSource dataSource;
    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    private boolean dbConnectionInfoPrinted;

    public String[] getLocations() {
        String[] result = new String[this.locations.getLocations().size()];
        for (int i = 0; i < this.locations.getLocations().size(); ++i) {
            result[i] = this.locations.getLocations().get(i).toString();
        }
        return result;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public String[] getSchemas() {
        return this.schemaNames;
    }

    public String getTable() {
        return this.table;
    }

    public MigrationVersion getTarget() {
        return this.target;
    }

    public Map<String, String> getPlaceholders() {
        return this.placeholders;
    }

    public String getPlaceholderPrefix() {
        return this.placeholderPrefix;
    }

    public String getPlaceholderSuffix() {
        return this.placeholderSuffix;
    }

    public String getSqlMigrationPrefix() {
        return this.sqlMigrationPrefix;
    }

    public String getSqlMigrationSeparator() {
        return this.sqlMigrationSeparator;
    }

    public String getSqlMigrationSuffix() {
        return this.sqlMigrationSuffix;
    }

    public boolean isIgnoreFailedFutureMigration() {
        return this.ignoreFailedFutureMigration;
    }

    public boolean isValidateOnMigrate() {
        return this.validateOnMigrate;
    }

    public boolean isCleanOnValidationError() {
        return this.cleanOnValidationError;
    }

    public MigrationVersion getInitVersion() {
        return this.initVersion;
    }

    public String getInitDescription() {
        return this.initDescription;
    }

    public boolean isInitOnMigrate() {
        return this.initOnMigrate;
    }

    public boolean isOutOfOrder() {
        return this.outOfOrder;
    }

    public MigrationResolver[] getResolvers() {
        return this.resolvers;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public void setIgnoreFailedFutureMigration(boolean ignoreFailedFutureMigration) {
        this.ignoreFailedFutureMigration = ignoreFailedFutureMigration;
    }

    public void setValidateOnMigrate(boolean validateOnMigrate) {
        this.validateOnMigrate = validateOnMigrate;
    }

    public void setCleanOnValidationError(boolean cleanOnValidationError) {
        this.cleanOnValidationError = cleanOnValidationError;
    }

    public void setLocations(String ... locations) {
        this.locations = new Locations(locations);
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setSchemas(String ... schemas) {
        this.schemaNames = schemas;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setTarget(MigrationVersion target) {
        this.target = target;
    }

    public void setTarget(String target) {
        this.target = MigrationVersion.fromVersion(target);
    }

    public void setPlaceholders(Map<String, String> placeholders) {
        this.placeholders = placeholders;
    }

    public void setPlaceholderPrefix(String placeholderPrefix) {
        this.placeholderPrefix = placeholderPrefix;
    }

    public void setPlaceholderSuffix(String placeholderSuffix) {
        this.placeholderSuffix = placeholderSuffix;
    }

    public void setSqlMigrationPrefix(String sqlMigrationPrefix) {
        this.sqlMigrationPrefix = sqlMigrationPrefix;
    }

    public void setSqlMigrationSeparator(String sqlMigrationSeparator) {
        if (!StringUtils.hasLength(sqlMigrationSeparator)) {
            throw new FlywayException("sqlMigrationSeparator cannot be empty!");
        }
        this.sqlMigrationSeparator = sqlMigrationSeparator;
    }

    public void setSqlMigrationSuffix(String sqlMigrationSuffix) {
        this.sqlMigrationSuffix = sqlMigrationSuffix;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.createdDataSource = false;
    }

    public void setDataSource(String url, String user, String password, String ... initSqls) {
        this.dataSource = new DriverDataSource(this.classLoader, null, url, user, password, initSqls);
        this.createdDataSource = true;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setInitVersion(MigrationVersion initVersion) {
        this.initVersion = initVersion;
    }

    public void setInitVersion(String initVersion) {
        this.initVersion = MigrationVersion.fromVersion(initVersion);
    }

    public void setInitDescription(String initDescription) {
        this.initDescription = initDescription;
    }

    public void setInitOnMigrate(boolean initOnMigrate) {
        this.initOnMigrate = initOnMigrate;
    }

    public void setOutOfOrder(boolean outOfOrder) {
        this.outOfOrder = outOfOrder;
    }

    public FlywayCallback[] getCallbacks() {
        return this.callbacks;
    }

    public void setCallbacks(FlywayCallback ... callbacks) {
        this.callbacks = callbacks;
    }

    public void setCallbacks(String ... callbacks) {
        List<FlywayCallback> callbackList = ClassUtils.instantiateAll(callbacks, this.classLoader);
        this.callbacks = callbackList.toArray(new FlywayCallback[callbacks.length]);
    }

    public void setResolvers(MigrationResolver ... resolvers) {
        this.resolvers = resolvers;
    }

    public void setResolvers(String ... resolvers) {
        List<MigrationResolver> resolverList = ClassUtils.instantiateAll(resolvers, this.classLoader);
        this.resolvers = resolverList.toArray(new MigrationResolver[resolvers.length]);
    }

    public int migrate() throws FlywayException {
        return this.execute(new Command<Integer>(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public Integer execute(Connection connectionMetaDataTable, Connection connectionUserObjects, DbSupport dbSupport, Schema[] schemas) {
                DbSupport dbSupportUserObjects;
                Schema originalSchemaUserObjects;
                boolean schemaChange;
                MetaDataTableImpl metaDataTable = new MetaDataTableImpl(dbSupport, schemas[0].getTable(Flyway.this.table), Flyway.this.classLoader);
                MigrationResolver migrationResolver = Flyway.this.createMigrationResolver(dbSupport);
                if (Flyway.this.validateOnMigrate) {
                    Flyway.this.doValidate(connectionMetaDataTable, connectionUserObjects, migrationResolver, metaDataTable, schemas, true);
                }
                new DbSchemas(connectionMetaDataTable, schemas, metaDataTable).create();
                if (!(metaDataTable.hasSchemasMarker() || metaDataTable.hasInitMarker() || metaDataTable.hasAppliedMigrations())) {
                    ArrayList<Schema> nonEmptySchemas = new ArrayList<Schema>();
                    for (Schema schema : schemas) {
                        if (schema.empty()) continue;
                        nonEmptySchemas.add(schema);
                    }
                    if (Flyway.this.initOnMigrate || nonEmptySchemas.isEmpty()) {
                        if (Flyway.this.initOnMigrate && !nonEmptySchemas.isEmpty()) {
                            new DbInit(connectionMetaDataTable, metaDataTable, Flyway.this.initVersion, Flyway.this.initDescription, Flyway.this.callbacks).init();
                        }
                    } else if (nonEmptySchemas.size() == 1) {
                        Schema schema = (Schema)nonEmptySchemas.get(0);
                        if (schema.allTables().length != 1 || !schema.getTable(Flyway.this.table).exists()) {
                            throw new FlywayException("Found non-empty schema " + schema + " without metadata table! Use init()" + " or set initOnMigrate to true to initialize the metadata table.");
                        }
                    } else {
                        throw new FlywayException("Found non-empty schemas " + StringUtils.collectionToCommaDelimitedString(nonEmptySchemas) + " without metadata table! Use init()" + " or set initOnMigrate to true to initialize the metadata table.");
                    }
                }
                boolean bl = schemaChange = !schemas[0].equals(originalSchemaUserObjects = (dbSupportUserObjects = DbSupportFactory.createDbSupport(connectionUserObjects, false)).getCurrentSchema());
                if (schemaChange) {
                    dbSupportUserObjects.setCurrentSchema(schemas[0]);
                }
                DbMigrate dbMigrate = new DbMigrate(connectionMetaDataTable, connectionUserObjects, dbSupport, metaDataTable, schemas[0], migrationResolver, Flyway.this.target, Flyway.this.ignoreFailedFutureMigration, Flyway.this.outOfOrder, Flyway.this.callbacks);
                try {
                    Integer n = dbMigrate.migrate();
                    return n;
                }
                finally {
                    if (schemaChange) {
                        dbSupportUserObjects.setCurrentSchema(originalSchemaUserObjects);
                    }
                }
            }
        });
    }

    public void validate() throws FlywayException {
        this.execute(new Command<Void>(){

            @Override
            public Void execute(Connection connectionMetaDataTable, Connection connectionUserObjects, DbSupport dbSupport, Schema[] schemas) {
                MetaDataTableImpl metaDataTable = new MetaDataTableImpl(dbSupport, schemas[0].getTable(Flyway.this.table), Flyway.this.classLoader);
                MigrationResolver migrationResolver = Flyway.this.createMigrationResolver(dbSupport);
                Flyway.this.doValidate(connectionMetaDataTable, connectionUserObjects, migrationResolver, metaDataTable, schemas, false);
                return null;
            }
        });
    }

    private void doValidate(Connection connectionMetaDataTable, Connection connectionUserObjects, MigrationResolver migrationResolver, MetaDataTable metaDataTable, Schema[] schemas, boolean pending) {
        String validationError = new DbValidate(connectionMetaDataTable, connectionUserObjects, metaDataTable, migrationResolver, this.target, this.outOfOrder, pending, this.callbacks).validate();
        if (validationError != null) {
            if (this.cleanOnValidationError) {
                new DbClean(connectionMetaDataTable, metaDataTable, schemas, this.callbacks).clean();
            } else {
                throw new FlywayException("Validate failed. Found differences between applied migrations and available migrations: " + validationError);
            }
        }
    }

    public void clean() {
        this.execute(new Command<Void>(){

            @Override
            public Void execute(Connection connectionMetaDataTable, Connection connectionUserObjects, DbSupport dbSupport, Schema[] schemas) {
                MetaDataTableImpl metaDataTable = new MetaDataTableImpl(dbSupport, schemas[0].getTable(Flyway.this.table), Flyway.this.classLoader);
                new DbClean(connectionMetaDataTable, metaDataTable, schemas, Flyway.this.callbacks).clean();
                return null;
            }
        });
    }

    public MigrationInfoService info() {
        return this.execute(new Command<MigrationInfoService>(){

            @Override
            public MigrationInfoService execute(Connection connectionMetaDataTable, Connection connectionUserObjects, DbSupport dbSupport, Schema[] schemas) {
                for (FlywayCallback callback : Flyway.this.getCallbacks()) {
                    callback.beforeInfo(connectionUserObjects);
                }
                MigrationResolver migrationResolver = Flyway.this.createMigrationResolver(dbSupport);
                MetaDataTableImpl metaDataTable = new MetaDataTableImpl(dbSupport, schemas[0].getTable(Flyway.this.table), Flyway.this.classLoader);
                MigrationInfoServiceImpl migrationInfoService = new MigrationInfoServiceImpl(migrationResolver, metaDataTable, Flyway.this.target, Flyway.this.outOfOrder, true);
                migrationInfoService.refresh();
                for (FlywayCallback callback : Flyway.this.getCallbacks()) {
                    callback.afterInfo(connectionUserObjects);
                }
                return migrationInfoService;
            }
        });
    }

    public void init() throws FlywayException {
        this.execute(new Command<Void>(){

            @Override
            public Void execute(Connection connectionMetaDataTable, Connection connectionUserObjects, DbSupport dbSupport, Schema[] schemas) {
                MetaDataTableImpl metaDataTable = new MetaDataTableImpl(dbSupport, schemas[0].getTable(Flyway.this.table), Flyway.this.classLoader);
                new DbSchemas(connectionMetaDataTable, schemas, metaDataTable).create();
                new DbInit(connectionMetaDataTable, metaDataTable, Flyway.this.initVersion, Flyway.this.initDescription, Flyway.this.callbacks).init();
                return null;
            }
        });
    }

    public void repair() throws FlywayException {
        this.execute(new Command<Void>(){

            @Override
            public Void execute(Connection connectionMetaDataTable, Connection connectionUserObjects, DbSupport dbSupport, Schema[] schemas) {
                MigrationResolver migrationResolver = Flyway.this.createMigrationResolver(dbSupport);
                MetaDataTableImpl metaDataTable = new MetaDataTableImpl(dbSupport, schemas[0].getTable(Flyway.this.table), Flyway.this.classLoader);
                new DbRepair(connectionMetaDataTable, migrationResolver, metaDataTable, Flyway.this.callbacks).repair();
                return null;
            }
        });
    }

    private MigrationResolver createMigrationResolver(DbSupport dbSupport) {
        PlaceholderReplacer placeholderReplacer = new PlaceholderReplacer(this.placeholders, this.placeholderPrefix, this.placeholderSuffix);
        return new CompositeMigrationResolver(dbSupport, this.classLoader, this.locations, this.encoding, this.sqlMigrationPrefix, this.sqlMigrationSeparator, this.sqlMigrationSuffix, placeholderReplacer, this.resolvers);
    }

    public void configure(Properties properties) {
        String callbacksProp;
        String resolversProp;
        String outOfOrderProp;
        String targetProp;
        String ignoreFailedFutureMigrationProp;
        String initOnMigrateProp;
        String initDescriptionProp;
        String initVersionProp;
        String validateOnMigrateProp;
        String cleanOnValidationErrorProp;
        String tableProp;
        String schemasProp;
        String encodingProp;
        String sqlMigrationSuffixProp;
        String sqlMigrationSeparatorProp;
        String sqlMigrationPrefixProp;
        String placeholderSuffixProp;
        String placeholderPrefixProp;
        String driverProp = properties.getProperty("flyway.driver");
        String urlProp = properties.getProperty("flyway.url");
        String userProp = properties.getProperty("flyway.user");
        String passwordProp = properties.getProperty("flyway.password");
        if (StringUtils.hasText(urlProp)) {
            this.setDataSource(new DriverDataSource(this.classLoader, driverProp, urlProp, userProp, passwordProp, new String[0]));
        } else if (!StringUtils.hasText(urlProp) && (StringUtils.hasText(driverProp) || StringUtils.hasText(userProp) || StringUtils.hasText(passwordProp))) {
            LOG.warn("Discarding INCOMPLETE dataSource configuration! flyway.url must be set.");
        }
        String locationsProp = properties.getProperty("flyway.locations");
        if (locationsProp != null) {
            this.setLocations(StringUtils.tokenizeToStringArray(locationsProp, ","));
        }
        if ((placeholderPrefixProp = properties.getProperty("flyway.placeholderPrefix")) != null) {
            this.setPlaceholderPrefix(placeholderPrefixProp);
        }
        if ((placeholderSuffixProp = properties.getProperty("flyway.placeholderSuffix")) != null) {
            this.setPlaceholderSuffix(placeholderSuffixProp);
        }
        if ((sqlMigrationPrefixProp = properties.getProperty("flyway.sqlMigrationPrefix")) != null) {
            this.setSqlMigrationPrefix(sqlMigrationPrefixProp);
        }
        if ((sqlMigrationSeparatorProp = properties.getProperty("flyway.sqlMigrationSeparator")) != null) {
            this.setSqlMigrationSeparator(sqlMigrationSeparatorProp);
        }
        if ((sqlMigrationSuffixProp = properties.getProperty("flyway.sqlMigrationSuffix")) != null) {
            this.setSqlMigrationSuffix(sqlMigrationSuffixProp);
        }
        if ((encodingProp = properties.getProperty("flyway.encoding")) != null) {
            this.setEncoding(encodingProp);
        }
        if ((schemasProp = properties.getProperty("flyway.schemas")) != null) {
            this.setSchemas(StringUtils.tokenizeToStringArray(schemasProp, ","));
        }
        if ((tableProp = properties.getProperty("flyway.table")) != null) {
            this.setTable(tableProp);
        }
        if ((cleanOnValidationErrorProp = properties.getProperty("flyway.cleanOnValidationError")) != null) {
            this.setCleanOnValidationError(Boolean.parseBoolean(cleanOnValidationErrorProp));
        }
        if ((validateOnMigrateProp = properties.getProperty("flyway.validateOnMigrate")) != null) {
            this.setValidateOnMigrate(Boolean.parseBoolean(validateOnMigrateProp));
        }
        if ((initVersionProp = properties.getProperty("flyway.initVersion")) != null) {
            this.setInitVersion(MigrationVersion.fromVersion(initVersionProp));
        }
        if ((initDescriptionProp = properties.getProperty("flyway.initDescription")) != null) {
            this.setInitDescription(initDescriptionProp);
        }
        if ((initOnMigrateProp = properties.getProperty("flyway.initOnMigrate")) != null) {
            this.setInitOnMigrate(Boolean.parseBoolean(initOnMigrateProp));
        }
        if ((ignoreFailedFutureMigrationProp = properties.getProperty("flyway.ignoreFailedFutureMigration")) != null) {
            this.setIgnoreFailedFutureMigration(Boolean.parseBoolean(ignoreFailedFutureMigrationProp));
        }
        if ((targetProp = properties.getProperty("flyway.target")) != null) {
            this.setTarget(MigrationVersion.fromVersion(targetProp));
        }
        if ((outOfOrderProp = properties.getProperty("flyway.outOfOrder")) != null) {
            this.setOutOfOrder(Boolean.parseBoolean(outOfOrderProp));
        }
        if (StringUtils.hasLength(resolversProp = properties.getProperty("flyway.resolvers"))) {
            this.setResolvers(StringUtils.tokenizeToStringArray(resolversProp, ","));
        }
        if (StringUtils.hasLength(callbacksProp = properties.getProperty("flyway.callbacks"))) {
            this.setCallbacks(StringUtils.tokenizeToStringArray(callbacksProp, ","));
        }
        HashMap<String, String> placeholdersFromProps = new HashMap<String, String>(this.placeholders);
        for (Object property : properties.keySet()) {
            String propertyName = (String)property;
            if (!propertyName.startsWith(PLACEHOLDERS_PROPERTY_PREFIX) || propertyName.length() <= PLACEHOLDERS_PROPERTY_PREFIX.length()) continue;
            String placeholderName = propertyName.substring(PLACEHOLDERS_PROPERTY_PREFIX.length());
            String placeholderValue = properties.getProperty(propertyName);
            placeholdersFromProps.put(placeholderName, placeholderValue);
        }
        this.setPlaceholders(placeholdersFromProps);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    <T> T execute(Command<T> command) {
        T result;
        Connection connectionMetaDataTable = null;
        Connection connectionUserObjects = null;
        try {
            if (this.dataSource == null) {
                throw new FlywayException("DataSource not set! Check your configuration!");
            }
            connectionMetaDataTable = JdbcUtils.openConnection(this.dataSource);
            connectionUserObjects = JdbcUtils.openConnection(this.dataSource);
            DbSupport dbSupport = DbSupportFactory.createDbSupport(connectionMetaDataTable, !this.dbConnectionInfoPrinted);
            this.dbConnectionInfoPrinted = true;
            LOG.debug("DDL Transactions Supported: " + dbSupport.supportsDdlTransactions());
            if (this.schemaNames.length == 0) {
                Schema currentSchema = dbSupport.getCurrentSchema();
                if (currentSchema == null) {
                    throw new FlywayException("Unable to determine schema for the metadata table. Set a default schema for the connection or specify one using the schemas property!");
                }
                this.setSchemas(currentSchema.getName());
            }
            if (this.schemaNames.length == 1) {
                LOG.debug("Schema: " + this.schemaNames[0]);
            } else {
                LOG.debug("Schemas: " + StringUtils.arrayToCommaDelimitedString(this.schemaNames));
            }
            Schema[] schemas = new Schema[this.schemaNames.length];
            for (int i = 0; i < this.schemaNames.length; ++i) {
                schemas[i] = dbSupport.getSchema(this.schemaNames[i]);
            }
            result = command.execute(connectionMetaDataTable, connectionUserObjects, dbSupport, schemas);
        }
        catch (Throwable throwable) {
            JdbcUtils.closeConnection(connectionUserObjects);
            JdbcUtils.closeConnection(connectionMetaDataTable);
            if (this.dataSource instanceof DriverDataSource && this.createdDataSource) {
                ((DriverDataSource)this.dataSource).close();
            }
            throw throwable;
        }
        JdbcUtils.closeConnection(connectionUserObjects);
        JdbcUtils.closeConnection(connectionMetaDataTable);
        if (this.dataSource instanceof DriverDataSource && this.createdDataSource) {
            ((DriverDataSource)this.dataSource).close();
        }
        return result;
    }

    static interface Command<T> {
        public T execute(Connection var1, Connection var2, DbSupport var3, Schema[] var4);
    }
}

