(ns calc.core
  "Este programa crea una calculadora simple con interaz grafica de
   usuario (GUI)."
  (:require
   [seesaw.core :refer [invoke-later button frame native! frame show!
                        grid-panel horizontal-panel vertical-panel
                        text text! pack! listen select to-root
                        config selection value]]
   [seesaw.dev  :refer [show-options]]
   [infix.macros :refer [infix]])
  (:gen-class))


;; Docs
;; https://cljdoc.org/d/seesaw/seesaw/1.5.0/api/seesaw.core?q=pack#pack!


(def visor-and-ce
  "Visor y boton de limpieza (CE)"
  (horizontal-panel
   :items [(text :id :visor :editable? false)
           (button :text "CE" :class "btn")]))

(def grid-buttons
  "Panel de botones"
  (grid-panel
   :columns 4
   :items [(button :text "1" :class "btn")
           (button :text "2" :class "btn")
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

(def calc-panel
  "Estructura de la calculadora"
  (vertical-panel
   :items [visor-and-ce grid-buttons]))

;; Escucha de eventos
;; Se utiliza la funcion select, a cual se le pasa como paraemtro el elemento
;; raiz de la aplicacinon y el o los IDs de los widgets que se van a escuchar
(listen (select calc-panel [:.btn]) ; Selector como CSS (# - ID, . - CLASS)
        :action (fn [e]
                  (let [visor-widget (select (to-root e) [:#visor])
                        current-value (text visor-widget)
                        button-value (config e :text)]
                    (case button-value
                      "CE" (text! visor-widget "0")
                      ;"=" (let [result (eval (read-string current-value))]
                      "+" (println "el valor es +")
                      "*" (println "el valor es *")
                      "/" (println "el valor es /")
                      "-" (println "el valor es -")
                      ;; Seteamos el visor con el valor actual + nuevo
                      (text! visor-widget (str current-value button-value))))))

(show-options (text))
;; https://github.com/rm-hull/infix
(infix 8 + 5)

(defn -main
  "Funcion principal de la calculadora"
  [& _args]
  (native!)
  (invoke-later
   (-> (frame :title "Calculadora",
              :content calc-panel ,
              :width 250,
              :height 170,
              :on-close :exit
              :resizable? false)
       ;pack! ; Contrae todos los widgets a su minimo tamaño
       show!)))
