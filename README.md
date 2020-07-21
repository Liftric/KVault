![CI](https://github.com/Liftric/kvault/workflows/CI/badge.svg) ![Published](https://github.com/Liftric/kvault/workflows/Publish%20to%20Bintray/badge.svg)

# KVault

KVault is a secure key-value store for Kotlin Multiplatform projects. It acts as a iOS Keychain wrapper and implements encrypted SharedPreferences for Android.

## Import

```kotlin
repositories {
    maven {  url = uri("https://dl.bintray.com/liftric/maven/") }
}
```

Then, simply add the dependencies to your sourceSets.

```kotlin
sourceSets {
    commonMain {
        dependencies {
            implementation("com.liftric:kvault:1.0"  )   
        }
    }
    androidMain {
        dependencies {
            implementation("com.liftric:kvault-android:1.0")
        }
    }
    iosMain {
        dependencies {
            implementation("com.liftric:kvault-ios:1.0")
        }
    }
}
```

## How-to

### Instantiating

#### Android

```kotlin
  val kVault = KVault(context = Context)
```

#### iOS

You can create an instance by using the singleton method. The singleton sets the service name (Used to identify keychain entries) to your main bundle identifier. In case that it can't retrieve the identifier it will be set to `com.liftric.KVault`. The access group (Identifier used to share keychains between apps) will be set to null.

```kotlin
  val kVault = KVault.shared()
  // or
  val kVault = KVault(serviceName = "com.company.identifier", val accessGroup = null)
```

### Setting

Objects can be inserted with the correspondending set method. 

```kotlin
  val stringStoredSuccessfully: Boolean = set(key = "PASSWORD", value = "546hfbfzzeujfdbfdz")
  val intStoredSuccessfully: Boolean = set(key = "SECRET", value = 45678765)
  val floatStoredSuccessfully: Boolean = set(key = "HEIGHT", value = 1.79)
```

#### Supported Types

- String
- Int
- Long
- Float
- Double
- Bool

### Getting

Casted values can be retrieved with type methods.

```kotlin
  val stringValue: String = string(forKey = "PASSWORD")
  val intValue: Int = int(forKey = "SECRET")
```

It's also possible to check if an object with a given key is in the keychain.

```kotlin
  val objectExists = existsObject(forKey = "PASSWORD")
```

### Deleting

Either a single object or all at once can be deleted.

```kotlin
  val removed: Boolean = removeObject(forKey = "PASSWORD")
```

```kotlin
  clear() // Removes all objects that are linked to the service name
```
