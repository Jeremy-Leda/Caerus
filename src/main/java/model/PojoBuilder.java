package model;

import model.validation.GenericPojoValidator;
import net.karneim.pojobuilder.GeneratePojoBuilder;

@GeneratePojoBuilder(withSetterNamePattern = "*", withCopyMethod = true, withValidator = GenericPojoValidator.class)
public @interface PojoBuilder {
}
