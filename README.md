# log4j2 appenders for Fluentd, Aws S3

`Minimum Requirements`
| Dependency      | Version       |
| :-------------  | :----------:  |
| Java            | 8             |
| log4j           | 2.11          |

Add `io.github.technologize` to `packages` in log4j's `Configuration`

## Sample log4j config

### Fluentd

`tag`: tag refers to tag of fluentd

```xml
<dependency>
  <groupId>io.github.technologize</groupId>
  <artifactId>fluentd-log4j-appender</artifactId>
  <version>1.0.0</version>
</dependency>
```

```xml
<Configuration strict="true" packages="io.github.technologize">
  <Appenders>
    <Fluentd name="fluentd" tag="yourTag" >
    <!-- 
      all fields are optional, fields name will be sent to fulentd as a key in json
      Field value/pattern can follow the Pattern as specified in PatternLayout  
      Refer: https://logging.apache.org/log4j/2.x/manual/layouts.html#PatternLayout
    -->
    <Field name="application">yourApplication</Field>
    <Field name="someOtherField">Otherfield %X{traceId}</Field>
    <Field name="lookupField" pattern="%N"/>   
    <!-- 
      all settings are optional, see FluencyBuilderForFluentd; for default values
      you can add as may fields as you like (or none at all)
    -->
    <FluentdConfig 
      maxBufferSize="536870912"
      bufferChunkInitialSize="1048576"
      bufferChunkRetentionSize="4194304"
      bufferChunkRetentionTimeMillis="1000"
      flushAttemptIntervalMillis="600"
      waitUntilBufferFlushed="10"
      waitUntilFlusherTerminated="10"
      senderMaxRetryCount="8"
      senderBaseRetryIntervalMillis="400"
      senderMaxRetryIntervalMillis="30000"
      connectionTimeoutMillis="5000"
      readTimeoutMillis="5000"
      ackResponseMode="true"
      sslEnabled="false"
      jvmHeapBufferMode="true"
      fileBackupDir="true">
      <!-- 
      all Servers are optional, locahost:24224 will be used if none are specified
      If multiple servers are specified,
        message will be sent to only one of them dependeing on availability
      --> 
      <Server host="localhost" port="24224" />
      <Server host="127.0.0.1" port="24224" />    
    </FluentdConfig>
    </Fluentd>
  </Appenders>

  <Loggers> 
    <root level="trace">
    <appender-ref ref="fluentd"/>
  </root>
  </Loggers>
</Configuration>
```

### Aws S3

`tag`: tag is the name of the Aws S3 bucket

```xml
<dependency>
  <groupId>io.github.technologize</groupId>
  <artifactId>aws-s3-log4j-appender</artifactId>
  <version>1.0.0</version>
</dependency>
```

```xml
<Configuration strict="true" packages="io.github.technologize">
  <Appenders>
    <AwsS3 name="AwsS3" tag="your bucket" >
      <!--
        all fields are optional, fields name will be sent to fulentd as a key in json
        Field value/pattern can follow the Pattern as specified in PatternLayout
        Refer: https://logging.apache.org/log4j/2.x/manual/layouts.html#PatternLayout
      -->
      <Field name="application">yourApplication</Field>
      <Field name="someOtherField">Otherfield %X{traceId}</Field>
      <Field name="lookupField" pattern="%d{dd MMM yyyy HH:mm:ss,SSS}"/>
      <!--
        all settings are optional, see FluencyBuilderForAwsS3 for default values
        you can add as may fields as you like (or none at all)

        If None are specified aws default configuration provider chain will be used

        formatTypes can be CSV, MESSAGE_PACK, JSONL(DEFAULT)
        formatCsvColumnNames are comma seperated Logging info to be present in CSV if format is CSV
        zoneOffsetId for valid values Refer 
          https://docs.oracle.com/javase/8/docs/api/java/time/ZoneOffset.html#of-java.lang.String-
      -->
      <AwsS3Config
        formatType= "JSONL"
        formatCsvColumnNames= "someOtherField, message, level"
        awsEndpoint= "your awsRegion"
        awsRegion= "your awsRegion"
        awsAccessKeyId= "your awsAccessKeyId"
        awsSecretAccessKey= "your awsSecretAccessKey"
        retryMax= "10"
        retryIntervalMillis= "1000"
        maxRetryIntervalMillis= "30000"
        retryFactor= "2"
        senderWorkBufSize= "8192"
        compressionEnabled= "false"
        s3KeyPrefix= "logs"
        s3KeySuffix= ".log.gz"
        zoneOffsetId= "Z"
        maxBufferSize="536870912"
        bufferChunkInitialSize="1048576"
        bufferChunkRetentionSize="4194304"
        bufferChunkRetentionTimeMillis="1000"
        flushAttemptIntervalMillis="600"
        fileBackupDir="/dir"
        waitUntilBufferFlushed="10"
        waitUntilFlusherTerminated="10"
        jvmHeapBufferMode="true">
      </AwsS3Config>
    </AwsS3>
  </Appenders>
  <Loggers>
    <Logger name="io.github.technologize" level="debug">
    <AppenderRef ref="AwsS3" />
    </Logger>
  </Loggers>
</Configuration>
```

Based on [fluency](https://github.com/komamitsu/fluency). Inspired from [log4j-plugin-fluency](https://github.com/wycore/log4j-plugin-fluency)
