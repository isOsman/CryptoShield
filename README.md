# CryptoShield  
<b>It my study project, and it does not claim to be good coding or cybersecurity practices.</b>   
Simple password manager.

<b>Auth</b>  
App implement primitive auth:   
1.In first use, app transhform user password to the MD5 hash and remember it   
2.In next using app transforms input password to the MD5 hash and compare this hash with remembered password hash  
If hashes is equals - user auth successful

<b>A little about AndroidKeyStore.</b>  
AndroidKeyStore allows us to store our cryptographic keys. The project uses a storage key that encrypts and decrypts user data.  
Accordingly, this key is stored in the AndroidKeyStore


<b>Encryption and storage user data</b>  
App uses two cipher alghoritms:  
1.AES  
2.RSA  

AES used for encrypt and decrypt user data.App generates AES key and saves in AndroidKeyStore.  
RSA used for get(enctrypt/decrypt) AES key in AndroidKeyStore.

<b>AES key</b>  
AES key saved in AndroidKeyStore.    
For get this AES key,used RSA key pair  
1.Encrypt AES with RSA-public key and saves to AndroidKeyStore  
2.Decrypt ecnrypted AES key with RSA-private key - so we get source(clear) AES key 

<b>Why RSA?</b>  
The fact is that in earlier versions of android, the android keystore used Asymmetric Encryption to store cryptographic keys; later versions use Symmetric Encryption.
To cover more devices, Asymmetric encryption was chosen.
It is worth mentioning that you can use adaptive code, which, depending on the android version, selects the encryption technology, but this is not implemented in this project.
 
<b>User data</b>    
App uses next alghoritm:   
Encryption  
1.User inputs data      
2.App encrypts data with AES key from AndroidKeyStore     
3.App add encrypted data to the user dataset

Decryption    
1.App load encrypted user dataset   
2.App decrypts data with AES key from AndroidKeyStore     
3.App show this data  

Below you can see approximate app scheme

![Encrypt](https://i.ibb.co/XC69dTF/scheme.png)

![Decrypt](https://i.ibb.co/KK3s3nv/dec.png)
