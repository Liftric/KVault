![CI](https://github.com/Liftric/kvault/workflows/CI/badge.svg) ![maven-central](https://img.shields.io/maven-central/v/com.liftric/kvault)

# KVault

KVault is a secure key-value storage for Kotlin Multiplatform projects. It acts as an iOS Keychain wrapper and implements encrypted SharedPreferences for Android.

## Import

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
  val store = KVault(context = Context)
```

#### iOS

```kotlin
  val store = KVault(serviceName = "com.company.identifier", accessGroup = null)
```

### Setting

Objects can be inserted with the corresponding set method. 

```kotlin
  val stringStoredSuccessfully: Boolean = store.set(key = "PASSWORD", value = "546hfbfzzeujfdbfdz")
  val intStoredSuccessfully: Boolean = store.set(key = "SECRET", value = 45678765)
  val floatStoredSuccessfully: Boolean = store.set(key = "HEIGHT", value = 1.79)
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
  val stringValue: String? = store.string(forKey = "PASSWORD")
  val intValue: Int? = store.int(forKey = "SECRET")
```

It's also possible to check if an object with a given key is in the keychain.

```kotlin
  val existsObject: Boolean = store.existsObject(forKey = "PASSWORD")
```

### Deleting

#### Single object

```kotlin
  val isRemoved: Boolean = store.removeObject(forKey = "PASSWORD")
```

#### All objects

##### iOS

If the service name and/or the access group is not null it will only delete the items that match the query.

```kotlin
  store.clear()
```

## License

KVault is available under the MIT license. See the LICENSE file for more info.
