package com.hmall.trade.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("order_logistics")
public class OrderLogistics implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "order_id", type = IdType.INPUT)
    private Long orderId;

    private String logisticsNumber;

    private String logisticsCompany;

    private String contact;

    private String mobile;

    private String province;

    private String city;

    private String town;

    private String street;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}