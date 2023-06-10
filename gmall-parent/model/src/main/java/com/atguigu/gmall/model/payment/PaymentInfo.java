//
//
package com.atguigu.gmall.model.payment;

import com.atguigu.gmall.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * PaymentInfo
 * </p>
 *
 */
@Data
@ApiModel(description = "支付信息")
@TableName("payment_info")
public class PaymentInfo extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "第三方支付地址")
	@TableField("pay_url")
	private String payUrl;

	@ApiModelProperty(value = "订单编号")
	@TableField("order_id")
	private Long orderId;

	@ApiModelProperty(value = "支付类型（微信 支付宝）")
	@TableField("payment_type")
	private String paymentType;
}

