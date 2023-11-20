package com.example.fuzzharnessautogenerator.dto.response;

import com.example.fuzzharnessautogenerator.dto.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RootResponse {
    private int responseCode;
    private String responseMessage;
    private Object responseData;
    private PageInfo pageInfo;
}
