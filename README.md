# log4j appenders based on [fluency](https://github.com/komamitsu/fluency)

Heavily inspired from [log4j-plugin-fluency](https://github.com/wycore/log4j-plugin-fluency) by

**Required:**

`tag`

To use these appenders, add `io.github.technologize` to `packages` in log4j's `Configuration`

`log4j >= 2.11`

Refer [examples](examples/)

## Fluentd

Add below to the appenders section.

```xml
<Configuration strict="true" packages="io.github.technologize">
 <Appenders>
  <Fluentd name="fluentd" tag="yourTag" >
   <!-- 
				all fields are optional, fields name will be sent to fulentd as a key in json
				Field value/pattern can follow the Pattern as specified in PatternLayout 
			-->
   <Field name="application">yourApplication</Field>
   <Field name="someOtherField">Otherfield %X{traceId}</Field>
   <Field name="lookupField" pattern="%N"/>   
   <!-- 
				all settings are optional, see defaultFluency() for default values
				you can add as may fields as you like (or none at all)
			-->
   <!-- <FluentdConfig/> -->
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
   <appender-ref ref="fluency"/>
 </root>
 </Loggers>
</Configuration>
```
