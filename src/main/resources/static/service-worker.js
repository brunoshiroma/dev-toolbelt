'use strict';

// CODELAB: Update cache names any time any of the cached files change.
const CACHE_NAME = 'static-cache-v1';

// CODELAB: Add list of files to cache here.
const FILES_TO_CACHE = [
    "/",
    "/index.html",
    "/offline.html",
    "/sha.html",
    "/js/common.js",
    "/js/sha.js"
];

self.addEventListener('install', (evt) => {
  console.log('[ServiceWorker] Install');
  self.skipWaiting();

  evt.waitUntil(
      caches.open(CACHE_NAME).then((cache) => {
        console.log('[ServiceWorker] Pre-caching offline page');
        return cache.addAll(FILES_TO_CACHE);
      })
  );
});

self.addEventListener('activate', (evt) => {
  console.log('[ServiceWorker] Activate');
  // CODELAB: Remove previous cached data from disk.

  //self.clients.claim();
});

self.addEventListener('sync', (evt) => {
    console.log('[ServiceWorker] sync', evt);
});

self.addEventListener('fetch', (evt) => {
  console.log('[ServiceWorker] Fetch', evt.request.url);

  evt.respondWith(
     caches.match(evt.request).then((response) => {
       return response ||
       fetch(evt.request)
       .catch((e) => {
         return caches.open(CACHE_NAME)
             .then((cache) => {
               return cache.match('/offline.html');
             });
       });
     })
   );

});