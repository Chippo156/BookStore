package com.book.book_store.model;

import com.book.book_store.common.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "Order")
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends AbstractEntity<Long>{
    @Column(name = "order_date",nullable = false)
    private LocalDateTime orderDate;

    @Enumerated
    @Column(name = "order_status",nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "total_amount",nullable = false)
    private BigDecimal orderTotal;
    @OneToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<OrderDetail> orderDetails;

}
