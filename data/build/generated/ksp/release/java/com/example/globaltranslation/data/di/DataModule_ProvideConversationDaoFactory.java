package com.example.globaltranslation.data.di;

import com.example.globaltranslation.data.local.ConversationDao;
import com.example.globaltranslation.data.local.ConversationDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
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
public final class DataModule_ProvideConversationDaoFactory implements Factory<ConversationDao> {
  private final Provider<ConversationDatabase> databaseProvider;

  private DataModule_ProvideConversationDaoFactory(
      Provider<ConversationDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public ConversationDao get() {
    return provideConversationDao(databaseProvider.get());
  }

  public static DataModule_ProvideConversationDaoFactory create(
      Provider<ConversationDatabase> databaseProvider) {
    return new DataModule_ProvideConversationDaoFactory(databaseProvider);
  }

  public static ConversationDao provideConversationDao(ConversationDatabase database) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideConversationDao(database));
  }
}
