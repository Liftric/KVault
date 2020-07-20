# KVault

Secure key-value store for Kotlin Multiplatform projects.

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
  val stringStoredSuccessfully: Boolean = set(value = "546hfbfzzeujfdbfdz", forKey = "PASSWORD")
  val intStoredSuccessfully: Boolean = set(value = 45678765, forKey = "SECRET")
  val floatStoredSuccessfully: Boolean = set(value = 1.79, forKey = "HEIGHT")
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
