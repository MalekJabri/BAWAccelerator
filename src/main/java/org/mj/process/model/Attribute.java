package org.mj.process.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Attribute implements Serializable {
    String display;
    String value;
}
