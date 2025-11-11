package com.kulvinder.livestream.domain.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GiftDto {

    private Long id;

    private String name;

    private String image;

    private Integer price;

}
