// thyleaf set with timestamp, on application.properties, created on build.gradle
const CACHE_NAME = '[( ${CACHE_VERSION} )]';

// CODELAB: Add list of files to cache here.
const FILES_TO_CACHE = [
    "/",
    "/offline",
    "/sha",
    "/password",
    "/url-encode",
    "/base64"
    "/js/common.js",
    "/js/sha.js",
    "/js/password.js",
    "/js/jquery-3.5.1.min.js",
    "/js/bootstrap.min.js",
    "/css/bootstrap.min.css"
];

self.addEventListener('install', (evt) => {
  console.log('[ServiceWorker] Install');
  self.skipWaiting();

  evt.waitUntil(
      caches
      .open(CACHE_NAME)
      .then((cache) => {
        console.log('[ServiceWorker] Pre-caching offline page');
        return cache.addAll(FILES_TO_CACHE);
      })
      .catch((e)=>{
        console.log(e);
      })
  );
});

//https://developers.google.com/web/ilt/pwa/caching-files-with-service-worker
self.addEventListener('activate', function(event) {
  event.waitUntil(
    caches.keys().then(function(cacheNames) {
      return Promise.all(
        cacheNames.filter(function(cacheName) {
            return cacheName != CACHE_NAME;
        }).map(function(cacheName) {
          return caches.delete(cacheName);
        })
      );
    })
  );
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
               return cache.match('/offline');
             });
       });
     })
   );

});