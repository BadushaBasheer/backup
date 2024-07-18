package com.socialmedia.socialmedia.dto;

import com.socialmedia.socialmedia.enums.ImageStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {

    private String id;

    private String title;

    private String description;

    private String imageUrl;

    private ImageStatus imageStatus;

    private Integer viewCount;

}
