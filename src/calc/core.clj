(ns calc.core
  "Este programa crea una calculadora simple con interaz grafica de
   usuario (GUI)."
  (:require
   ;; https://github.com/clj-commons/seesaw
   [seesaw.core :refer [invoke-later button frame native! frame show! grid-panel
                        horizontal-panel vertical-panel text text! pack! listen
                        select to-root config selection value]]
   [seesaw.dev  :refer [show-options]]
   [seesaw.keymap :refer [map-key]]
   ;; https://github.com/rm-hull/infix
   [infix.macros :refer [from-string]])
  (:gen-class))

(def visor-and-ce
  "Visor y boton de limpieza (CE)"
  (horizontal-panel
   :items [(text :id :visor :editable? false)
           (button :text "CE" :class "btn")]))

(def grid-buttons
  "Panel de botones"
  (grid-panel
   :columns 4
   :items [(button :text "1" :class "btn" :id "btn1")
           (button :text "2" :class "btn" :id "btn2")
           (button :text "3" :class "btn")
           (button :text "+" :class "btn")
           (button :text "4" :class "btn")
           (button :text "5" :class "btn")
           (button :text "6" :class "btn")
           (button :text "-" :class "btn")
           (button :text "7" :class "btn")
           (button :text "8" :class "btn")
           (button :text "9" :class "btn")
           (button :text "/" :class "btn")
           (button :text "0" :class "btn")
           (button :text "." :class "btn")
           (button :text "=" :class "btn")
           (button :text "*" :class "btn")]))

(defn button-actions
  "Funcion que aplica un evento a todos los botones de la calculadora"
  [e] ;; ! NOTE: Cambiar esa e por algo descriptivo
  (let [visor-widget (select (to-root e) [:#visor])
        current-value (text visor-widget)
        button-value (config e :text)]
    (case button-value
      "CE" (text! visor-widget "")
      ;; from-string (de la libreria infix) parsea una cadena de texto como
      ;; "2+2" ejecuta la operacion y devuelve el valor como un string
      "=" (text! visor-widget
                 (str (try ((from-string current-value))
                           (catch Exception _e "Err")))) ; x / 0 = Err

      (text! visor-widget (str current-value button-value)))))

(def calc-panel
  "Estructura de la calculadora"
  (vertical-panel
   :items [visor-and-ce grid-buttons]))

;; Escucha de eventos
;; Se utiliza la funcion select, a cual se le pasa como paraemtro el elemento
;; raiz de la aplicacinon y el o los IDs de los widgets que se van a escuchar
(listen (select calc-panel [:.btn]) ; Selector como CSS (# - ID, . - CLASS)
        :action (fn [e] (button-actions e)))


;; ! TODO: En teoria la siguiente funcion puede hacer que al presionar una tecla
;; !       se ejecute el evento click de un boton
(def valid-keys
  '("1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "+", "-", "*", "/"))

;(map #(map-key calc-panel % (select calc-panel [:#btn1])) valid-keys)
;; ! NOTE: No anda
(for [k valid-keys]
  (map-key calc-panel k (select calc-panel [(keyword (str "#btn" k))])))

;; ! NOTE: El siguiente anda
;(map-key calc-panel "1" (select calc-panel [:#btn1]))

;(show-options (text))
;; https://github.com/rm-hull/infix

(defn -main
  "Funcion principal de la calculadora"
  [& _args]
  (native!)
  (invoke-later
   (-> (frame :title "Calculadora",
              :content calc-panel ,
              :width 250,
              :height 170,
              ;:size [250 :by 300]
              :on-close :exit
              :resizable? false)
       ;pack! ; Contrae todos los widgets a su minimo tamaño
       show!)))
