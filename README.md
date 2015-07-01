# Auto Mortar

Mortar / Flow / Dagger 2 requires to write a lot of boilerplate code.  
Auto Mortar is an annotation processor that focuses on eliminating the maximum of that boilerplate. No magic tricks here, just some straightforward and human readable code generated for you.


### "Traditional" way

```java 
// ShowUserScreen.java

@Layout(R.layout.screen_show_user)
public class ShowUserScreen extends Path {

    private String username;

    public ShowUserScreen(String username) {
        this.username = username;
    }
    
    // We also need a method that creates the dagger2 component
    // either by reflection or by some method like the following
    public Component createComponent(RootActivity.Component parent) {
	    return DaggerShowUserScreen.Component.builder()
	    	.component(parent)
	    	.module(new Module())
	    	.build();
    }


    @dagger.Component(dependencies = RootActivity.Component.class, modules = Module.class)
    @DaggerScope(ShowUserPresenter.class)
    public interface Component extends AppDependencies {
        void inject(ShowUserView view);
    }

    @dagger.Module
    public class Module {

        @Provides
        @DaggerScope(ShowUserPresenter.class)
        public Presenter providePresenter(RestClient restClient) {
            return new Presenter(username, restClient);
        }
    }

    public static class Presenter extends ViewPresenter<ShowUserView> {

        private final String username;
        private final RestClient restClient;

        public Presenter(String username, RestClient restClient) {
            this.username = username;
            this.restClient = restClient;
        }
    }
}

```

### With Auto Mortar

You only write the Presenter class.

```java
// ShowUserPresenter.java

@AutoScreen(
        component = @AutoComponent(dependencies = RootActivity.class, superinterfaces = AppDependencies.class),
        screenAnnotations = Layout.class
)
@DaggerScope(ShowUserPresenter.class)
@Layout(R.layout.screen_show_user)
public class ShowUserPresenter extends ViewPresenter<ShowUserView> {

    private final String username;
    private final RestClient restClient;

    public ShowUserPresenter(@ScreenParam String username, RestClient restClient) {
        this.username = username;
        this.restClient = restClient;
    }
}

```

### The big picture

1. Create the presenter class (recommanded name syntax is `XyzPresenter`)
2. Annotate the presenter with `@AutoScreen`
3. Rebuild the project in order to trigger the annotation processor
4. Use `XyzScreen` and `XyzScreenComponent` as you wish :)


### How does it work

The annotation processor generates during compilation 3 classes for each `@AutoScreen` annotated presenter.  
For the ShowUserPresenter class, it will generate:

- ShowUserScreen
- ShowUserScreenComponent
- ShowUserScreen.Module

All the generated code is readable and accessible in your IDE, in the same way Dagger 2 does.


#### Screen

The generated screen is named XyzScreen, while your presenter should be named XyzPresenter.  
The screen contains the generated Module as a static subclass.  

The generated screen can be annoted with your custom annotation.  
In order to do so, you have to first annotate the presenter with that annotation, and then specify the annotation class in `@AutoScreen screenAnnotations` member.

For instance, if you have a `@Layout` annotation you want to apply on the generated screen:

```java
@AutoScreen(
	screenAnnotations = Layout.class
)
@Layout(R.layout.my_layout)
public class PostsPresenter {}
```

The generated screen will look like:

```java
@Layout(2130903043) // equals to R.layout.my_layout
public final class PostsScreen {}
```

If you configured Auto Mortar to make the generated screens extend from `Path`, you can then use them with Flow.

```java
`Flow.get(context).set(new ViewPostScreen(1234))`
```

To see how to configure Auto Mortar and how to pass navigation parameters between screens, see below.


#### Component

The component generation relies on the Auto Dagger2 library.  
See the readme of Auto Dagger2 for details: [https://github.com/lukaspili/Auto-Dagger2](https://github.com/lukaspili/Auto-Dagger2)


#### Module

The generated module is named XyzScreen.Module and declares a provider method for the presenter. The right dependencies will be injected.  
**The presenter must use constructor injection for its dependencies. Field or setter injection is not supported.**

Generated module looks like:

```java
@dagger.Module
  public class Module {
    @Provides
    @ScreenScope(YourNamePresenter.class)
    public YourNamePresenter providePresenter(RestClient restClient) {
      return new YourNamePresenter(restClient);
    }
  }
```


### Auto Mortar configuration

You can also customize the code generation through a configuration annotation.  
Create a new empty interface (or class), and annotate it with `@AutoMortarConfig`. All configuration options are exposed as members of `@AutoMortarConfig`.

```java
@AutoMortarConfig(
	screenSuperclass = Path.class // all generated screens will extend from Path
)
interface Config { }
```

If you don't provide a configuration, the default will be used (see `automortar.config.DefaultAutoMortarConfig`). Only one configuration per project is supported.


### Passing parameters between screens

When you navigate from one screen to another, you often want to pass some parameters.  
`@AutoScreen` generates all for you. You just have do declare the navigation parameters in your presenter, like a normal dependecy injected by dagger.  
The only difference is that you have to annotate it with `@ScreenParam` in the presenter constructor.

```java
public class ShowUserPresenter extends ViewPresenter<ShowUserView> {

    private final String username; // username will be provided by another screen through Flow navigation
    private final RestClient restClient; // restclient is provided by some dagger component

    public ShowUserPresenter(@ScreenParam String username, RestClient restClient) {
        this.username = username;
        this.restClient = restClient;
    }
}
```

That's all! `@AutoScreen` will generate the following screen and module for you.

```java
public final class ShowUserScreen {
  private String username;

  public ShowUserScreen(String username) {
    this.username = username;
  }

  @dagger.Module
  public class Module {
    @Provides
    @ScreenScope(ShowUserPresenter.class)
    public ShowUserPresenter providePresenter(RestClient restClient) {
      return new ShowUserPresenter(username, restClient);
    }
  }
}
```

Finally, navigate between screens like you would normally do:  
`Flow.get(context).set(new ShowUserScreen("lukasz"))`


### Component factory and helper

When using Mortar and Flow together, you would setup a context factory that setups the mortar context associated with the screen to display. You would use `DaggerService.createComponent()` to create the component associated to the screen, using reflection.  
`@AutoScreen` generates for you the method that create the component without reflection.
The generated screen implements a `ScreenComponentFactory` interface that declares the createComponent method. It takes the component dependency as parameter, and returns an instance of the component. It looks like:

```java
public final class ViewPostScreen implements ScreenComponentFactory<RootActivityComponent> {

  @Override
  public Object createComponent(RootActivityComponent dependency) {
    return DaggerViewPostScreenComponent.builder()
    	.component(dependency)
    	.module(new Module())
    	.build();
  }
```

The generated screen provides also a helper static get method that retreives the component from the context:

```java
public final class ViewPostScreen implements ScreenComponentFactory {

  public static ViewPostScreenComponent getComponent(Context context) {
    return (ViewPostScreenComponent) context.getSystemService(AutoMortarConfig.DAGGER_SERVICE_NAME);
  }
```


### Dagger scope

In the same way as Auto Dagger2 works, you need to annotate your presenter with a dagger scope annotation (an annotation that is itself annotated with `@Scope`).
Auto mortar will detect this annotation, and will apply it on the generated module and component.

If you don't provide scope annotation on the presenter, the generated module and component will be unscoped.


## Demo

Check the sample project for demo.  
You can also check a full demo project with Auto Mortar here: 
[https://github.com/lukaspili/Power-Mortar-Flow-Dagger2-demo](https://github.com/lukaspili/Power-Mortar-Flow-Dagger2-demo)


## Installation

Beware that the groupId changed to **com.github.lukaspili.automortar**

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:1.1.3'
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.4'
    }
}

apply plugin: 'com.android.application'
apply plugin: 'com.neenbedankt.android-apt'

repositories {
    jcenter()
}

dependencies {
    apt 'com.github.lukaspili.automortar:automortar-compiler:1.1'
    compile 'com.github.lukaspili.automortar:automortar:1.1'
    
    apt 'com.github.lukaspili.autodagger2:autodagger2-compiler:1.1'
    compile 'com.github.lukaspili.autodagger2:autodagger2:1.1'

    apt 'com.google.dagger:dagger-compiler:2.0.1'
    compile 'com.google.dagger:dagger:2.0.1'
    provided 'javax.annotation:jsr250-api:1.0'
}
```


## Status

Stable API.


## Author

- Lukasz Piliszczuk ([@lukaspili](https://twitter.com/lukaspili))


## License

Auto Mortar is released under the MIT license. See the LICENSE file for details.


