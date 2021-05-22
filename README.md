![CI](https://github.com/Liftric/kvault/workflows/CI/badge.svg) ![maven-central](https://img.shields.io/maven-central/v/com.liftric/kvault)

# KVault

KVault is a secure key-value storage for Kotlin Multiplatform projects. It acts as an iOS Keychain wrapper and implements encrypted SharedPreferences for Android.

## Import

Simply add the dependencies to your sourceSets:

```kotlin
sourceSets {
    val commonMain by getting {
        dependencies {
            implementation("com.liftric:kvault:<version>")
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

```kotlin
  val kVault = KVault(serviceName = "com.company.identifier", accessGroup = null)
```

##### Convencience

Service name will be set to your main bundle identifier and the access group to null. In case that it can't retrieve the identifier it will be set to `com.liftric.KVault`.

```kotlin
  val kVault = KVault.Default
```

### Setting

Objects can be inserted with the corresponding set method. 

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
  val existsObject: Boolean = existsObject(forKey = "PASSWORD")
```

### Deleting

Delete a single object.

```kotlin
  val isRemoved: Boolean = removeObject(forKey = "PASSWORD")
```

Delete all objects.

```kotlin
  clear()
```

## License

KVault is available under the MIT license. See the LICENSE file for more info.
