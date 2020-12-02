package com.voltaire.restaurant.model;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {

    @DocumentId
    private String id;

    private String idField;

    private String name;

    private BigDecimal price;

    private String description;

}
