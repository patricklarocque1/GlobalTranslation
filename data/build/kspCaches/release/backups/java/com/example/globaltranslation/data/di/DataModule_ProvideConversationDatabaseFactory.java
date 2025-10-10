package com.example.globaltranslation.data.di;

import android.content.Context;
import com.example.globaltranslation.data.local.ConversationDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DataModule_ProvideConversationDatabaseFactory implements Factory<ConversationDatabase> {
  private final Provider<Context> contextProvider;

  private DataModule_ProvideConversationDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ConversationDatabase get() {
    return provideConversationDatabase(contextProvider.get());
  }

  public static DataModule_ProvideConversationDatabaseFactory create(
      Provider<Context> contextProvider) {
    return new DataModule_ProvideConversationDatabaseFactory(contextProvider);
  }

  public static ConversationDatabase provideConversationDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideConversationDatabase(context));
  }
}
