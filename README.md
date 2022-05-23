# ITLab - приложение под Android

## Trick to sign app everywhere
> A simple tutorial to sign your app on remote build machine
1. Get your keystore file. For example, `key.jks`
1. Convert keystore to `.txt` file via `openssl` and `base64` tools:
    ```bash
    openssl base64 < `key.jks` | tr -d '\n' | tee keystore.txt
    ```
    > Linux has this tools preinstalled
1. Content of `keystore.txt` use as you want:
    1. Copy to github secrets
    1. Copy to Azure DevOps pipeline variables
    1. Copy somewhere else in a secure place
1. Parse `keystore.txt` back
    ```bash
    echo keystore.txt | base64 --decode > keystore.jks
    ```
1. Use `keystore.jks` as it was in step 1
