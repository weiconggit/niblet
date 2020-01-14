package org.weicong.common.constant;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @description 请求返回体
 * @author weicong
 * @date 2020年1月14日 
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Rp<E> implements Serializable{
	
	private static final long serialVersionUID = -939399577904238648L;

    /**
     * @code : 响应状态码
     */
    private Integer code;

    /**
     * @msg : 该状态码对应的提示信息
     */
    private String msg;

    /**
     * @data : 响应数据
     */
    private E data;

    public Rp(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    
    /**
     * 无数据返回成功信息
     * @param <E>
     * @return
     */
    public static <E> Rp<E> buildSuccess() {
        return Rp.build(RpEnum.SUCCESS, null);
    }
    
    /**
     * 返回查询结果信息
     * @param <E>  返回数据类型
     * @param data 返回数据
     * @return
     */
    public static <E> Rp<E> buildSuccess(E data) {
        return Rp.build(RpEnum.SUCCESS, data);
    }
    
    public static <E> Rp<E> build(RpEnum rpEnum) {
        return new Rp<E>(rpEnum.code, rpEnum.msg);
    }

    public static <E> Rp<E> build(RpEnum rpEnum, E data) {
        return new Rp<E>(rpEnum.code, rpEnum.msg).setData(data);
    }

    public static <E> Rp<E> build(Integer code, String msg) {
        return new Rp<E>(code, msg);
    }

}
