(ns clj-http-client.feed-parser
  (:import
    (com.sun.syndication.io SyndFeedInput XmlReader)
    (java.io InputStream)))

(defstruct +feed+         :channel :entries)
(defstruct +feed-channel+ :title :description :link :pub-date)
(defstruct +feed-entry+   :title :description :link :pub-date :guid)

(defn parse-feed
  "Given an InputStream for the feed data, returns a +feed+ struct and with
  corresponding substructs."
  [#^InputStream source]
  (let [synd-feed (.build (SyndFeedInput.) (XmlReader. source))]
       (struct +feed+
         (struct +feed-channel+
           (.getTitle         synd-feed)
           (.getDescription   synd-feed)
           (.getLink          synd-feed)
           (.getPublishedDate synd-feed))
         (map
           (fn [synd-feed-entry]
             (struct +feed-entry+
               (.getTitle         synd-feed-entry)
               (.getValue         (.getDescription synd-feed-entry))
               (.getLink          synd-feed-entry)
               (.getPublishedDate synd-feed-entry)
               (.getUri           synd-feed-entry)))
           (.getEntries synd-feed)))))
