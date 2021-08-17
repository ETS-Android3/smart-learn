'use strict';

const functions = require("firebase-functions");
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

const db = admin.firestore()


const COLLECTION_USERS = "users";

exports.userAdded = functions.auth.user().onCreate(user => {

        // Add initial data related to this user
        const creationTime = Date.now();
        const data = {
                documentMetadata: {
                        owner: user.uid == null ? "" : user.uid,
                        createdAt: creationTime,
                        modifiedAt: creationTime,
                        searchList: [],
                        counted: true
                },
                accountMarkedForDeletion: false,
                email: user.email == null ? "" : user.email,
                profilePhotoUrl: user.photoURL == null ? "" : user.photoURL,
                displayName: user.displayName == null ? "" : user.displayName,
                receivedRequest:[],
                pendingFriends:[],
                friends:[],
                nrOfUnreadNotifications: 0,
                nrOfLessons: 0,
                nrOfWords: 0,
                nrOfExpressions: 0,
                nrOfOnlineInProgressTests: 0,
                nrOfOnlineFinishedTests: 0,
                nrOfLocalUnscheduledInProgressTests: 0,
                nrOfLocalUnscheduledFinishedTests: 0,
                nrOfLocalScheduledTests: 0,
                totalSuccessRate: 0.0
              };

        // Create user document with initial data. Use merge option in order to merge with existing
        // document if document already exists.
        db.collection(COLLECTION_USERS).doc(user.uid).set(data, {merge: true});

        console.log("Added document: " + user.uid);
        return Promise.resolve();
})

exports.userDeleted = functions.auth.user().onDelete(user => {
        // when a user deletes his account, just mark the account for deletion without deleting data
        // TODO: try to give an interval for restoring the account if the user decides to return back
        db.collection(COLLECTION_USERS).doc(user.uid).update({accountMarkedForDeletion: true});
        console.log("Marked for deletion document: " + user.uid);
        return Promise.resolve();
})


/** **************************************************************************************
 *                                 For testing purposes
 * ***************************************************************************************/

// Create and Deploy Your First Cloud Functions
// https://firebase.google.com/docs/functions/write-firebase-functions

// exports.helloWorld = functions.https.onRequest((request, response) => {
//         functions.logger.info("Hello logs!", {structuredData: true});
//         response.send("Hello from Firebase!");
// });

const COLLECTION_NOTIFICATIONS = "notifications";
const COLLECTION_FRIENDS = "friends";
const COLLECTION_LESSONS = "lessons";
const COLLECTION_WORDS = "words";
const COLLECTION_EXPRESSIONS = "expressions";
const COLLECTION_LOCAL_TESTS = "local_tests";


exports.addTestData = functions.https.onRequest((request, response) => {
        const userUidList = ["JxoSVwcvzH82BXNmMnjhTlkDC2SU", "klIrWUhgeb8UmN87BJyDI0QR75xK"];
        const size = userUidList.length;
        for (let i = 0; i < size; i++){
                 // https://www.w3schools.com/js/js_random.asp
                // Returns a random integer from 0 to 20
                const randomNrOfLessons = Math.floor(Math.random() * 21);
                const randomNrOfNotifications = Math.floor(Math.random() * 21);
                const randomNrOfTests = Math.floor(Math.random() * 21);

                addLessonsWithWordsAndExpressions(randomNrOfLessons, userUidList[i]);
                addNotifications(randomNrOfNotifications, userUidList[i]);
                addLocalTests(randomNrOfTests, userUidList[i]);
                addFriends(userUidList, userUidList[i]);
                console.log("\nAdded test data for user UID = [" + userUidList[i] + "]\n");
        }

        response.send("Added test data for users UID = [" + userUidList + "]");
});

function addLessonsWithWordsAndExpressions(nrOfLessons, userUid){
        const lessonCollection = db.collection(COLLECTION_USERS).doc(userUid).collection(COLLECTION_LESSONS);
        
        for (let i = 0; i < nrOfLessons; i++){

                // https://www.w3schools.com/js/js_random.asp
                // Returns a random integer from 0 to 20
                const randomNrOfWords = Math.floor(Math.random() * 21);
                const randomNrOfExpressions = Math.floor(Math.random() * 21);

                const data = {
                        documentMetadata: {
                                owner: userUid,
                                createdAt: 1234,
                                modifiedAt: 1234,
                                searchList: ["value_1", "values_2"],
                                counted: true
                        },
                        notes: "notes value",
                        type: 1,
                        name: "name value",
                        nrOfWords: randomNrOfWords,
                        nrOfExpressions: randomNrOfExpressions
                };

                const lessonDocRef = lessonCollection.doc();
                lessonDocRef.set(data);

                addWords(randomNrOfWords, userUid, lessonDocRef.id);
                addExpressions(randomNrOfExpressions, userUid, lessonDocRef.id);

       }

       console.log("Added [" + nrOfLessons + "] lessons for user UID = [" + userUid + "]");
}

function addWords(nrOfWords, userUid, lessonDocumentId){
        const wordsCollection = db.collection(COLLECTION_USERS).doc(userUid)
                                        .collection(COLLECTION_LESSONS).doc(lessonDocumentId).collection(COLLECTION_WORDS);
        
        for (let i = 0; i < nrOfWords; i++){
                const data = {
                        documentMetadata: {
                                owner: userUid,
                                createdAt: 1234,
                                modifiedAt: 1234,
                                searchList: ["value_1", "values_2"],
                                counted: true
                        },
                        notes: "notes value",
                        isFavourite: false,
                        language: "language value",
                        isFromSharedLesson: false,
                        ownerDisplayName: "owner display name value",
                        translations: "translations value",
                        statistics: {
                                correctAnswers: 0,
                                totalExtractions: 0,
                                totalTimeForCorrectAnswers: 0,
                                totalTimeForWrongAnswers: 0,
                                score: 0.0
                        },
                        word: "words value",
                        phonetic: "phonetic value"


                };

                wordsCollection.doc().set(data);
       }

       console.log("Added [" + nrOfWords + "] words for lesson [" + lessonDocumentId + "] for user UID = [" + userUid + "]");
}


function addExpressions(nrOfExpressions, userUid, lessonDocumentId){
        const expressionsCollection = db.collection(COLLECTION_USERS).doc(userUid)
                                                .collection(COLLECTION_LESSONS).doc(lessonDocumentId).collection(COLLECTION_EXPRESSIONS);
        
        for (let i = 0; i < nrOfExpressions; i++){
                const data = {
                        documentMetadata: {
                                owner: userUid,
                                createdAt: 1234,
                                modifiedAt: 1234,
                                searchList: ["value_1", "values_2"],
                                counted: true
                        },
                        notes: "notes value",
                        isFavourite: false,
                        language: "language value",
                        isFromSharedLesson: false,
                        ownerDisplayName: "owner display name value",
                        translations: "translations value",
                        statistics: {
                                correctAnswers: 0,
                                totalExtractions: 0,
                                totalTimeForCorrectAnswers: 0,
                                totalTimeForWrongAnswers: 0,
                                score: 0.0
                        },
                        expression: "expression value",
                };

                expressionsCollection.doc().set(data);
       }

       console.log("Added [" + nrOfExpressions + "] expressions for lesson [" + lessonDocumentId + "] for user UID = [" + userUid + "]");
}

function addNotifications(nrOfNotifications, userUid){
        const userDocRef = db.collection(COLLECTION_USERS).doc(userUid);
        const notificationsCollection = db.collection(COLLECTION_USERS).doc(userUid).collection(COLLECTION_NOTIFICATIONS);
        
        for (let i = 0; i < nrOfNotifications; i++){

                const data = {
                        documentMetadata: {
                                owner: userUid,
                                createdAt: 1234,
                                modifiedAt: 1234,
                                searchList: ["value_1", "values_2"],
                                counted: true
                        },
                        fromUid: "from uid value",
                        fromDisplayName: "from display name value",
                        fromDocumentReference: userDocRef,
                        type: 1,
                        markedAsRead: true,
                        hidden: false,
                        finished: true,
                        extraInfo: "extra info value",
                        message: "message value",
                        accepted: false,
                        declined: false,
                        receivedLesson: "received lesson value",
                        receivedLessonWordList: "received words value",
                        receivedLessonExpressionList: "received expressions value"
                };

                notificationsCollection.doc().set(data);
       }

       console.log("Added [" + nrOfNotifications + "] notifications for user UID = [" + userUid + "]");
}


function addLocalTests(nrOfLocalTests, userUid){
        const localTestsCollection = db.collection(COLLECTION_USERS).doc(userUid).collection(COLLECTION_LOCAL_TESTS);
        
        for (let i = 0; i < nrOfLocalTests; i++){

                const data = {
                        documentMetadata: {
                                owner: userUid,
                                createdAt: 1234,
                                modifiedAt: 1234,
                                searchList: ["value_1", "values_2"],
                                counted: true
                        },
                        // general test
                        type: 1,
                        testName: "test name value",
                        customTestName: "custom test name value",
                        testGenerationDate: 1234,
                        successRate: 0.0,
                        hidden: false,
                        finished: false,
                        generated: false,
                        questionsJson: "questions json value",
                        totalQuestions: 0,
                        answeredQuestions: 0,
                        correctAnswers: 0,
                        testTotalTime: 0,
                        useCustomSelection: false,
                        nrOfValuesForGenerating: 0,
                        questionCounter: -1,
                        scheduled: false,
                        scheduleActive: false,
                        hour: -1,
                        minute: -1,
                        oneTime: false,
                        dayOfMonth: -1,
                        month: -1,
                        year: -1,
                        alarmId: -1,
                        daysStatus: [false, false, false, false, false, false, false],
                        lessonId: "lesson id value",
                        lessonName: "lesson name value",
                        sharedLesson: false,
                        // specific for test document
                        countedAsFinished: false,
                        online: false,
                        containerTestId: "container test id value",
                        participants: ["participant_1","participant_2"],
                        userDisplayName: "user display name value",
                        userEmail: "user email value",
                        userProfilePhotoUrl: "user photo url value",
                        alarmDeviceId: "alarm device id value",
                        alarmWasLaunched: false
                };

                localTestsCollection.doc().set(data);
       }

       console.log("Added [" + nrOfLocalTests + "] local tests for user UID = [" + userUid + "]");
}

function addFriends(friendUidList, userUid){
        const userDocRef = db.collection(COLLECTION_USERS).doc(userUid);
        const friendsCollection = db.collection(COLLECTION_USERS).doc(userUid).collection(COLLECTION_FRIENDS);

        let cnt = 0;
        const size = friendUidList.length;
        for (let i = 0; i < size; i++){
                
                // current user is not added in hist firend list
                if(friendUidList[i] === userUid){
                        continue;
                }

                const data = {
                        documentMetadata: {
                                owner: userUid,
                                createdAt: 1234,
                                modifiedAt: 1234,
                                searchList: [],
                                counted: true
                        },
                        accountMarkedForDeletion: false,
                        email: "friend email",
                        displayName: "friend display name",
                        profilePhotoUrl: "friend photo url",
                        friendUid: friendUidList[i],
                        friendsSince: 1234,
                        userDocumentReference: userDocRef
                      };
        
                friendsCollection.doc().set(data);
                // add friend in user document list also
                userDocRef.update({friends: admin.firestore.FieldValue.arrayUnion(friendUidList[i])});
                cnt++;
       }

       console.log("Added [" + cnt + "] friends for user UID = [" + userUid + "]");
}