package fun.powercheng.url.tiny.model.po;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Created by PowerCheng on 2024/12/28.
 */
@Data
@Table("t_url_mapping")
@Builder
public class UrlMapping {

    @Id
    private Long id;

    /**
     * 短 URL 编码
     */
    @Column("short_code")
    private String shortCode;

    /**
     * 原始 URL
     */
    @Column("original_url")
    private String originalUrl;
}
