# MVP for Mortar / Flow / Dagger 2

MVP with Mortar / Flow / Dagger 2 is very nice. However it requires to write a lot of boilerplate code.  
Mortar MVP is an annotation processor that focuses on eliminating the maximum of that boilerplate. No magic tricks here, just some straightforward and human readable code generated for you.


### "Traditional" way

```java 
// ShowUserScreen.java

@Layout(R.layout.screen_show_user)
public class ShowUserScreen extends Path {

    private String username;

    public ShowUserScreen(String username) {
        this.username = username;
    }

    @dagger.Component(dependencies = RootActivity.Component.class, modules = Module.class)
    @PerScreenScope(Component.class)
    public interface Component extends RootActivity.Component {
        void inject(ShowUserView view);
    }

    @dagger.Module
    public class Module {

        @Provides
        @PerScreenScope(Component.class)
        public Presenter providePresenter(RestClient restClient) {
            return new Presenter(username, restClient);
        }
    }

    @PerScreenScope(Component.class)
    public static class Presenter extends ViewPresenter<ShowUserView> {

        private final String username;
        private final RestClient restClient;

        public Presenter(String username, RestClient restClient) {
            this.username = username;
            this.restClient = restClient;
        }
    }
}


// ShowUserView.java

public class ShowUserView extends LinearLayout {

    @Inject
    ShowUserScreen.Presenter presenter;

    public ShowUserView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerService.<ShowUserScreen.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.takeView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        presenter.dropView(this);
        super.onDetachedFromWindow();
    }
}
```

### With Mortar MVP annotation processor

```java

// ShowUserPresenter.java

@MVP(
        parentComponent = RootActivity.Component.class,
        baseViewLayout = LinearLayout.class,
        screenAnnotations = Layout.class
)
@Layout(R.layout.screen_show_user)
public class ShowUserPresenter extends ViewPresenter<MVP_ShowUserScreen.View> {

    private final String username;
    private final RestClient restClient;

    @Inject
    public ShowUserPresenter(@ScreenParam String username, RestClient restClient) {
        this.username = username;
        this.restClient = restClient;
    }
}


// ShowUserView.java

public class ShowUserView extends MVP_ShowUserScreen.View {

    public ShowUserWithMVPView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
```

### How does it look like

1. Create the presenter class (recommanded name syntax is `SomethingPresenter`)
2. Create the constructor annotated with `@Inject`
3. Annotate the presenter with `@MVP`
4. Rebuild the project in order to trigger the annotation processor
5. Create the view and make it extend from `MVP_Something.View` (or skip this step if you don't want base view)
6. Make the presenter extend from `ViewPresenter<MyView>`
7. Rebuild the project in order to trigger the annotation processor again
8. Enjoy the 10min you saved from writing boilerplate code :)


### How does it work

The annotation processor generates during compilation up to 4 classes for each `@MVP` annotated presenter.  
For the ShowUserPresenter class, it will generate:

- MVP_ShowUserScreen
- MVP_ShowUserScreen.Component
- MVP_ShowUserScreen.Module
- MVP_ShowUserScreen.View (if necessary)

All the generated code is readable and accessible in your IDE, in the same way dagger 2 does.

#### Screen

The generated screen is named MVP_YourNameScreen, while your presenter should be named YourNamePresenter.  
Depending which library extension you use, the screen will extend from flow.path.Path, flownavigation.path.Path or a custom classe (or none). More details below.

The screen contains all the other associated generated classes (Component, Module, View).

The generated screen can be annoted with your custom annotation.  
In order to do so, you have to first annotate the presenter with that annotation, and then specify the annotation class in `@MVP screenAnnotations` member.

For instance, if you have a `@Layout` annotation you want to apply on the generated screen:

```java
@MVP(
	screenAnnotations = Layout.class
)
@Layout(R.layout.my_layout)
public class ViewPostPresenter {}
```

The generated screen will look like:

```java
@Generated("mvp.compiler.AnnotationProcessor")
@Layout(2130903043) // equals to R.layout.my_layout
public final class MVP_PostsScreen {}
```

In order to use the screen instance, like in navigation between screens with Flow, use:  
`Flow.get(context).set(new MVP_YourNameScreen())`

For navigation parameters between screens, see below.


#### Component

The generated component is named MVP_YourNameScreen.Component. It declares that the associated view of the presenter can be injected.

In the `@MVP` annotation, you must declare the parent component that defines the generated component dependency.

```java
@MVP(
	parentComponent = RootActivity.Component.class,
)
```

And the generated component looks like:

```java
@dagger.Component(
    dependencies = mvp.sample.ui.activity.RootActivity.Component.class,
    modules = Module.class
)
@ScreenScope(Component.class)
public interface Component extends mvp.sample.ui.activity.RootActivity.Component {
    void inject(View view);
}
```

#### Module

The generated module is named MVP_YourName.Module and declares a provider method for the presenter. The right dependencies will be injected.  
**The presenter must use constructor injection for its dependencies. `@Inject` on field parameters does not work for now.**

Generated module looks like:

```java
@dagger.Module
  public class Module {
    @Provides
    @ScreenScope(Component.class)
    public YourNamePresenter providePresenter(RestClient restClient) {
      return new YourNamePresenter(restClient);
    }
  }
```


#### View

The view associated to the presenter must contain some boilerplate code that can also be generated (injection, inflation, butterknife, etc). It will generate a base view named `MVP_YourNameScreen.View` from which you real view can then extend. Or if your view already has all that code, you can just skip the base view generation.

**With base view**

You must define what will be the superclass of the base view (LinearLayour, RelativeLayout, etc).

```java
@MVP(
	baseViewLayout = LinearLayout.class,
)
```

The generated base view class looks like:

```java
public abstract static class View extends LinearLayout {
    @Inject
    protected YourNamePresenter presenter;

    public View(Context context) {
      super(context);
      init(context);
    }

    public View(Context context, AttributeSet attrs) {
      super(context, attrs);
      init(context);
    }

    public View(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init(context);
    }

    protected void init(Context context) {
      ((Component)context.getSystemService(DaggerService.SERVICE_NAME)).inject(this);
    }

    @Override
    public void onFinishInflate() {
      super.onFinishInflate();
      ButterKnife.inject(this);
    }

    @Override
    public void onAttachedToWindow() {
      super.onAttachedToWindow();
      presenter.takeView((PostsView)this);
    }

    @Override
    public void onDetachedFromWindow() {
      presenter.dropView((PostsView)this);
      super.onDetachedFromWindow();
    }
}
```

**Without base view**

You must define the class of the view associated to the presenter.

```java
@MVP(
	view = MyView.class,
)
```

**You cannot use `view` and `baseViewLayout` attributes together.**

To resume the different uses:

**With `baseViewLayout` attribute:**  

- generates MVP_XXX.View: the baseView from which the real view will extend
- contains the boilerplate (inject, takeView, dropView, etc)
- presenter must then extend from ViewPresenter<RealView>

**With `view` attribute:**

- does not generate MVP_XXX.View
- uses directly the real view
- you must write the view boilerplate yourself
- presenter must then extend from ViewPresenter<RealView>


## More uses

### Configuration

You can also customize the code generation through a configuration annotation.  
Create a new empty interface (or class), and annotate it with `@MvpConfiguration`. All configuration options are exposed as members of `@MvpConfiguration`.

```java
@MvpConfiguration(
	screenSuperclass = Path.class // all generated screens will extend from Path
)
interface MvpConfig { }
```

If you don't provide a configuration, the default will be used (see `mvp.config.DefaultMvpConfiguration`). And you can't have several configurations in the current version.

### Passing parameters between screens

When you navigate from one screen to another, you often want to pass some parameters.  
`@MVP` generates all for you. You just have do declare the navigation parameters in your presenter, like a normal dependecy injected by dagger.  
The only difference is that you have to annotate it with `@ScreenParam` in the presenter constructor.

```java
public class ShowUserPresenter extends ViewPresenter<MVP_ShowUserScreen.View> {

    private final String username; // username will be provided by another screen through Flow navigation
    private final RestClient restClient; // restclient is provided by some dagger component

    @Inject
    public ShowUserPresenter(@ScreenParam String username, RestClient restClient) {
        this.username = username;
        this.restClient = restClient;
    }
}
```

That's all! `@MVP` will generate the following screen and module for you.

```java
public final class MVP_ShowUserScreen {
  private String username;

  public MVP_ShowUserScreen(String username) {
    this.username = username;
  }

  @dagger.Module
  public class Module {
    @Provides
    @ScreenScope(Component.class)
    public ShowUserPresenter providePresenter(RestClient restClient) {
      return new ViewPostPresenter(username, restClient);
    }
  }

  // ...
}
```

Finally, navigate between screens like you would normally do:  
`Flow.get(context).set(new MVP_ShowUserScreen("lukasz"))`


### Component factory

When using Mortar and Flow together, you would setup a context factory that setups the mortar context associated with the screen to display. You would use `DaggerService.createComponent()` to create the component associated to the screen, using reflection.  
`@MVP` generates for you the method that create the component without reflection.
The generated screen implements a `ComponentFactory` interface that declares the createComponent method. This method takes the parent component as parameter, and returns an instance of the component. It looks like:

```java
public final class MVP_ViewPostScreen extends Path implements ComponentFactory<RootActivity.Component> {

  @Override
  public Object createComponent(mvp.sample.ui.activity.RootActivity.Component parentComponent) {
    return DaggerMVP_ViewPostScreen_Component.builder()
    	.component(parentComponent)
    	.module(new Module())
    	.build();
  }
```


### Dagger scope

Each generated screen, and its associated component and presenter will be scoped using the `@ScreenScope(Component.class)` (where Component is the generated component).  
ScreenScope is a dagger scope annotation provided by Mortar MVP.


### DaggerService

Since Mortar 0.17, the dagger2support was removed.  
Mortar MVP provides a `DaggerService` class that works pretty much the same like the old one.


## Installation

Gradle apt plugin recommended, like for dagger 2.

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:1.1.3'
        classpath 'com.github.dcendents:android-maven-plugin:1.2'
    }
}

apply plugin: 'com.android.application'
apply plugin: 'com.neenbedankt.android-apt'

repositories {
    jcenter()
    maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
}

dependencies {
    apt 'com.github.lukaspili:mortar-mvp-compiler:0.2-SNAPSHOT'
    compile 'com.github.lukaspili:mortar-mvp:0.2-SNAPSHOT'
}
```


## Status

Experimental library. Probably lots of edge cases not covered.  
Discussions and feedback welcomed, please open an issue.  
Or drop a word on gitter: [https://gitter.im/lukaspili/Mortar-MVP](https://gitter.im/lukaspili/Mortar-MVP)


## Flow-navigation

Flow-navigation is another experimental library upon Flow library.  
It's an alternative to Flow-path that preserves the scopes of previous paths in navigation history. You can check it out here: [https://github.com/lukaspili/flow-navigation](https://github.com/lukaspili/flow-navigation)


## Author

- Lukasz Piliszczuk ([@lukaspili](https://twitter.com/lukaspili))


## License

Mortar MVP is released under the MIT license. See the LICENSE file for more info.


