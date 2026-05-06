package com.pos.platform.common.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结构
 */
@Data
@NoArgsConstructor
public class PageResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<T> list;
    private Pagination pagination;

    @Data
    @NoArgsConstructor
    public static class Pagination implements Serializable {
        private int page;
        private int pageSize;
        private long total;
        private int totalPages;

        public Pagination(int page, int pageSize, long total) {
            this.page = page;
            this.pageSize = pageSize;
            this.total = total;
            this.totalPages = (int) Math.ceil((double) total / pageSize);
        }
    }

    public static <T> PageResponse<T> of(List<T> list, int page, int pageSize, long total) {
        PageResponse<T> response = new PageResponse<>();
        response.setList(list);
        response.setPagination(new Pagination(page, pageSize, total));
        return response;
    }
}