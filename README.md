## Description 

It is an **Android** application that can be used to create personal lessons (dictionaries) by storing pairs of the *word/expression - translation/meaning* type. Later, the application can use these pairs in order to generate various tests that can be used to practice the added words/expressions. 

#### Auth roles and related functionalities:
* **guest** (no account required):
    * can add and modify lessons (including words, expressions, translations)
    * can create local tests (including scheduled tests) in order to practice the added words/expressions
* **user** (account required):
    * all functionalities available to the **guest** role
    * ability to have friends
    * ability to share and receive lessons
    * ability to create common lessons with multiple contributors
    * ability to create online tests with multiple participants

## Used technologies related to the auth roles

* both **guest** and **user**:
  - Java language
  - [MVVM arhitecture]( https://developer.android.com/jetpack/guide)
  - [Android Jetpack](https://developer.android.com/jetpack) 
    - [View Binding](https://developer.android.com/topic/libraries/view-binding)
    - [Data Binding](https://developer.android.com/topic/libraries/data-binding)
    - [Live Data](https://developer.android.com/topic/libraries/architecture/livedata)
    - [View Model](https://developer.android.com/topic/libraries/architecture/viewmodel)
    - [Navigation Component](https://developer.android.com/guide/navigation/navigation-getting-started)
  
 * **guest**:
   - Local storage using [Room ORM library](https://developer.android.com/training/data-storage/room) (using *SQLite*)

 * **user**:
   - User authentication using [Firebase Authentication](https://firebase.google.com/docs/auth)
   - Cloud functions using [Firebase Functions](https://firebase.google.com/docs/functions)
   - Online storage using [Firebase Firestore](https://firebase.google.com/docs/firestore) (*NoSQL*)
   - Online storage for the profile images using [Firebase Storage](https://firebase.google.com/docs/storage)

## Demo:

### Guest

https://user-images.githubusercontent.com/37272520/135762330-462a212e-3ccd-49a6-9f0e-21571b6a2879.mp4

### User

https://user-images.githubusercontent.com/37272520/135762332-1a167a2a-0397-49ea-8d75-3894c930f987.mp4
