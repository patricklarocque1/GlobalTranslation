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
public final class MlKitTextRecognitionProvider_Factory implements Factory<MlKitTextRecognitionProvider> {
  @Override
  public MlKitTextRecognitionProvider get() {
    return newInstance();
  }

  public static MlKitTextRecognitionProvider_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static MlKitTextRecognitionProvider newInstance() {
    return new MlKitTextRecognitionProvider();
  }

  private static final class InstanceHolder {
    static final MlKitTextRecognitionProvider_Factory INSTANCE = new MlKitTextRecognitionProvider_Factory();
  }
}
