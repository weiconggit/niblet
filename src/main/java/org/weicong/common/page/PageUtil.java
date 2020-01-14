package org.weicong.common.page;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.weicong.common.exception.BizException;
import org.weicong.common.util.StringUtil;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @description
 * @author weicong
 * @date 2020年1月14日
 * @version 1.0
 */
public abstract class PageUtil<T> {

	// 默认每页记录数
	private static final Long DEFAULT_PAGE_SIZE = 10L;

	/**
	 * 将 分页请求对象 转换为 MyBatis Plus 分页对象
	 * 
	 * @param pageVO 分页请求对象
	 * @return MyBatis Plus 分页对象
	 */
	public static <T> IPage<T> toMybatisPage(PageRQ pageRQ) {
		Page<T> page = new Page<>();
		if (null == pageRQ)
			return page;

		page.setOptimizeCountSql(true);
		page.setSearchCount(true);
		page.setSize(pageRQ.getSize() == 0L ? DEFAULT_PAGE_SIZE : pageRQ.getSize());
		page.setCurrent(pageRQ.getCurrent());
		
		if (!StringUtil.isBlank(pageRQ.getSortRule())) {
			String[] sort = pageRQ.getSortRule().split("\\.");
			String[] col = sort[1].split(",");
			switch (sort[0].toLowerCase()) {
			case "asc":
				page.setAsc(col);
				break;
			case "desc":
				page.setDesc(col);
				break;
			default:
				break;
			}
		}
		return page;
	}

	/**
	 * 转换为分页返回对象， 用于自定义 page 方法
	 * 
	 * @param <T>   返回列表数据的类型
	 * @param iPage mybatis plus 分页对象
	 * @return
	 */
	public static <T> PageRP<T> toPage(IPage<?> iPage) {
		PageRP<T> pageRP = new PageRP<>();
		if (null == iPage)
			return pageRP;

		pageRP.setCurrent(iPage.getCurrent())
			.setPages(iPage.getPages())
			.setSize(iPage.getSize())
			.setTotal(iPage.getTotal());
		return pageRP;
	}

	/**
	 * 转换为 分页返回对象， 用于 mybatis plus 提供的 page 方法
	 * 
	 * @param <T>   返回列表数据的类型
	 * @param iPage mybatis plus 分页对象
	 * @param clazz 返回列表数据Class的类型
	 * @return
	 */
	public static <T> PageRP<T> toPage(IPage<?> iPage, Class<?> clazz) {
		PageRP<T> pageRP = new PageRP<>();
		if (null == iPage)
			return pageRP;

		pageRP.setCurrent(iPage.getCurrent())
			.setPages(iPage.getPages())
			.setSize(iPage.getSize())
			.setTotal(iPage.getTotal());

		if (!CollectionUtils.isEmpty(iPage.getRecords())) {

			@SuppressWarnings("unchecked")
			List<T> list = iPage.getRecords().stream().map(val -> {

				T t = null;
				try {
					t = (T) clazz.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					throw new BizException("分页数据转换错误");
				}
				BeanUtils.copyProperties(val, t);
				return t;

			}).collect(Collectors.toList());

			pageRP.setList(list);
		}

		return pageRP;
	}

}
