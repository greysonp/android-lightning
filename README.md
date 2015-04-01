# Lightning
Take notes super fast.

## Setup

### API Keys
This app requires that you have API keys with the cloud storage providers we use. If you'd like to do development,
you'll need to get your own keys. The app expects the following string resources to be available:

```
R.string.dropbox_app_key
R.string.dropbox_app_secret
```

It should be pretty clear what they are. The file ```app/src/main/res/values/api_keys.xml``` is already added to the
```.gitignore``` file. It is recommended that you define your API string resources there.