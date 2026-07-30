package KMA.SmartEdu.core.common;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    // data của trang hiện tại
    private List<T> content;

    // trang hiện tại
    private int pageNumber;

    // elements tối đa trên 1 trang
    private int pageSize;

    // elements có trong toàn bộ db
    private long totalElements;

    // tổng số trang
    private int totalPages;

    // marking đây có phải là trang cuối cùng hay không
    private boolean isLast;
}