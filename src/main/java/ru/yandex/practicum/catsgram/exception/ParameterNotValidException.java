package ru.yandex.practicum.catsgram.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ParameterNotValidException extends IllegalArgumentException {
    private String parameter;
    private String reason;

}
