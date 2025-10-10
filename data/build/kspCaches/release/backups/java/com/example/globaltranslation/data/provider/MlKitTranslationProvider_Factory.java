package com.example.globaltranslation.data.provider;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class MlKitTranslationProvider_Factory implements Factory<MlKitTranslationProvider> {
  @Override
  public MlKitTranslationProvider get() {
    return newInstance();
  }

  public static MlKitTranslationProvider_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static MlKitTranslationProvider newInstance() {
    return new MlKitTranslationProvider();
  }

  private static final class InstanceHolder {
    static final MlKitTranslationProvider_Factory INSTANCE = new MlKitTranslationProvider_Factory();
  }
}
