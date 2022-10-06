package com.sqlpro.l7cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.listener.ChannelTopic;

@Getter
@Configuration
@ConfigurationProperties(prefix = "spring.l7cache")
@RequiredArgsConstructor
public class L7CacheProperties {

    /**
     * Cache Expire for TTL (ms)
     * 삽입 또는 업데이트 후 항목이 만료된 후 기간입니다.
     * 만료는 타이머 이벤트를 통해 발생하며 기본적으로 약 1초 지연될 수 있습니다. 해당 부분은 timerLag(long, TimeUnit)를 참조하기 바랍니다.
     * 정확한 만료를 위해서는 ExpiryPolicy를 지정하고 sharpExpiry(boolean)를 활성화해야 합니다.
     * ExpiryPolicy가 이 값과 함께 지정되면 최대 만료 기간이 여기에 지정된 값으로 제한됩니다.
     * 0 또는 org.cache2k.expiry.ExpiryTimeValues.NOW 값은 모든 항목이 즉시 만료되어야 함을 의미합니다. 구성별로 캐싱을 비활성화하는 데 유용할 수 있습니다.
     * 시간 기반 만료를 끄려면 영원(부울)을 사용하십시오.
     * Long.MAX_VALUE 밀리초 이상의 값(다른 단위가 사용되는 경우)은 영구 만료로 처리됩니다.
     * 마지막 액세스 시간을 기반으로 캐시 항목을 제거하고 활동이 적은 시간에 캐시를 축소하려면 idleScanTime(long, TimeUnit) 매개변수를 사용할 수 있습니다.
     */
    @Value("${ttl:3600000}")
    private long ttl;

    /**
     * Redis Pub/Sub Topic
     */
    @Value("${topic:l7-cache}")
    private String topic;

    /**
     * Cache name
     */
    @Value("${name:L7-CACHE}")
    private String name;

    /**
     * Eternal Value
     */
    @Value("${eternal:true}")
    private boolean eternal;

    /**
     * 캐시가 보유하는 최대 항목 수입니다.
     * 최대 크기에 도달하면 크기를 유지하기 위해 기존 항목을 하나 이상 제거합니다.
     * Long.MAX_VALUE 값은 용량이 제한되지 않음을 의미합니다.
     * 기본값은 Cache2kConfig.DEFAULT_ENTRY_CAPACITY입니다.
     * 이는 기본적으로 무제한 캐시를 생성하는 Guava 또는 Caffeine과 같은 캐시와 다릅니다. 잠재적으로 메모리 누수가 될 수 있기 때문입니다.
     */
    @Value("${entry-capacity:-1}")
    private int entryCapacity;

    @Value("${sharp-expiry:false}")
    private boolean sharpExpiry;
}
