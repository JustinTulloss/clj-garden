(ns stash.utils
  (:use clojure.set
        clojure.contrib.except
        clojure.contrib.str-utils))

(defn update
  "'Updates' a value in an associative structure, where k is a key and f is a 
  function that will take the old value and any supplied args and return the new 
  value, and returns the new associative structure."
  [m k f & args]
  (assoc m k (apply f (get m k) args)))

(defn mash
  "Reduce a seq-able to a map. The given fn should return a 2-element tuple
  representing a key and value in the new map."
  [f coll]
  (reduce
    (fn [memo elem]
      (let [[k v] (f elem)]
        (assoc memo k v)))
    {} coll))

(defn with-assoc-meta
  "Returns an object with the key and value assoced onto its meta data."
  [obj k v]
  (with-meta obj (assoc (meta obj) k v)))

(defn update-meta-by
  "Returns an object with metadata at the key updated from its current value
  by the function."
  [obj k f]
  (let [m (meta obj)]
    (with-meta obj (assoc m k (f (get m k))))))

(defmacro def-
  "Like def, but creates a private var."
  [sym form]
  `(def ~(with-assoc-meta sym :private true) ~form))

(defmacro get-or
  "Short for (or (get map key) or-form)."
  [map key or-form]
  `(or (get ~map ~key) ~or-form))

(defn limit-keys
  "Assures that the given map has only keys included in recognized-keys."
  [map recognized-keys]
  (let [bad (reduce #(dissoc %1 %2) map recognized-keys)]
    (if-not (empty? bad)
      (throwf "Unrecognized keys %s" (pr-str (keys bad))))))

(defn the-str
  "Returns the name of the val if it is a clojure.lang.Name, otherwise val."
  [val]
  (if (instance? clojure.lang.Named val) (name val) val))

(defn re-matches?
  "Returns true iff the given string contains a match for the given pattern."
  [#^java.util.regex.Pattern pattern string]
  (.find (.matcher pattern string)))