(ns cljDCPU.core)

(def memory (ref {}))
(def registers (ref {}))
(def register-conversion {0 :a, 1 :b, 2 :c, 3 :x, 4 :y, 5 :z, 6 :i, 7 :j})

(defn get-register
  [n]
  (let [n (if-not (keyword? n) (register-conversion n) n)]
    (get @registers n 0)))

(defn set-register
  [n f]
  (let [n (if-not (keyword? n) (register-conversion n) n)]
    (dosync (alter registers f))))

(defn inc-register
  [name]
  (set-register name inc))

(defn dec-register
  [name]
  (set-register name dec))

(defn change-register
  [name value]
  (set-register name #(assoc % name value)))

(defn get-memory
  "Fetch the provided memory address. Valid addresses are 0x0 to 0x10000.
   No check is done to verify the provided address is within the range.
   Assume the default value of memory is 0x0"
  [address]
  (get @memory address 0))

(defn set-memory
  "Set the memory address with value. Valid addresses are 0x0 to 0x10000.
   As each word is 16 bits, the max value is 0xFFFF. If the provided value
   is greater than 0xFFFF the value saved will be value mod 0x10000"
  [address value]
  (dosync
   (alter memory assoc address (mod value 0x10000))))

(defn- mask-and-shift
  "Generates a function which applies a bit mask to a word and then
   right shifts the result"
  [mask shift]
  (fn [word]
    (-> word
        (bit-and mask)
        (bit-shift-right shift))))

(def get-o
  "retrieves the opcode from the word"
  (mask-and-shift 0xF 0))

(def get-a
  "retrieves the a parameter from the word"
  (mask-and-shift 0x3F0 4))

(def get-b
  "retrieves the b parameter from the word"
  (mask-and-shift 0xFC00 10))

(defn -main
  "I don't do a whole lot."
  [& args]
  (println "Hello, World!"))