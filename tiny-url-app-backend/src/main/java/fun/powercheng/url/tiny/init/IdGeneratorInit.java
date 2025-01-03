package fun.powercheng.url.tiny.init;

import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import fun.powercheng.url.tiny.config.TinyUrlAppConfig;
import fun.powercheng.url.tiny.enums.ShortenerTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Created by PowerCheng on 2024/12/29.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IdGeneratorInit implements ApplicationRunner {

    private final TinyUrlAppConfig appConfig;

    /**
     * 机器序列号部分设置 4 位，最多可支持 2^4 = 16 台机器
     */
    private static final int WORKER_ID_BIT_LENGTH = 4;

    /**
     * 每毫秒生成的序列号 设置为 5 位 2^5 = 32 每毫秒 32 个 ID  一秒就可以生成 32 * 1000 = 3.2 w 个 ID
     */
    private static final int SEQ_BIT_LENGTH = 4;

    @Override
    public void run(ApplicationArguments args) {
        if (appConfig.getShortenerType() != ShortenerTypeEnum.UNIQUE_ID_WITH_BASE62) {
            return;
        }
        IdGeneratorOptions options = new IdGeneratorOptions(appConfig.getWorkerId());
        options.WorkerIdBitLength = WORKER_ID_BIT_LENGTH;
        options.SeqBitLength = SEQ_BIT_LENGTH;
        // 最长共 41(时间戳) + 4 + 5 = 50 2^50 = 1125899906842624（16位） base62 之后 59i69UgKW（9位）
        YitIdHelper.setIdGenerator(options);
        log.info("雪花算法 ID 生成器初始化完成");
    }
}
