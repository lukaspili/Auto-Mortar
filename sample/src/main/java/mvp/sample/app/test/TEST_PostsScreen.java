package mvp.sample.app.test;

import android.content.Context;
import autodagger.AutoComponent;
import dagger.Provides;
import flownavigation.common.flow.Layout;
import flownavigation.path.Path;
import generatedmvp.MVP_Config;
import java.lang.Object;
import java.lang.Override;
import javax.annotation.Generated;
import mvp.ScreenComponentFactory;
import mvp.sample.app.DaggerScope;
import mvp.sample.app.presenter.PostsPresenter;
import mvp.sample.rest.RestClient;
import mvp.sample.ui.activity.RootActivity;
import mvp.sample.ui.activity.RootActivity_Component;

@Generated("mvp.compiler.AnnotationProcessor")
@AutoComponent(
    target = PostsPresenter.class,
    modules = TEST_PostsScreen.Module.class,
    dependencies = RootActivity.class
)
@DaggerScope(PostsPresenter.class)
@Layout(2130903043)
public final class TEST_PostsScreen extends Path implements ScreenComponentFactory {
  @Override
  public Object createComponent(Object... dependencies) {
    return DaggerTEST_PostsScreen_Component.builder()
    	.rootActivity_Component((RootActivity_Component)dependencies[0])
    	.module(new Module())
    	.build();
  }

  public static TEST_PostsScreen_Component getComponent(Context context) {
    return (TEST_PostsScreen_Component) context.getSystemService(MVP_Config.DAGGER_SERVICE_NAME);
  }

  @dagger.Module
  public class Module {
    @Provides
    @DaggerScope(PostsPresenter.class)
    public PostsPresenter providePresenter(RestClient restClient) {
      return new PostsPresenter(restClient);
    }
  }
}
