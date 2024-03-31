;; Calculadora simple
;;
;; El siguiente proyecto es una calculadora simple escrita en el lenguaje de
;; programación Clojure.
;;
;; Con fines didactios para aprender el lenguaje y como proyecto para entregar
;; en la asignatura de 'Programacíon Logica'.
;;
;; Librerias utilizadas.
;;
;; - [seesaw.core](https://github.com/clj-commons/seesaw)
;; - [infix.macros](hhttps://github.com/rm-hull/infix)
;;
;; by Benabhi <benabhi@gmail.com> (c) 2024.
(ns calc.core
  "Este programa crea una calculadora simple con interaz grafica de
   usuario (GUI)."
  (:require
   [seesaw.core :refer [invoke-later button frame native! frame show! grid-panel
                        horizontal-panel vertical-panel text text! listen config
                        select to-root repaint!]]
   ;; ? NOTE: Para ver las opciones de un widget
   ;;[seesaw.dev  :refer [show-options]]
   [seesaw.keymap :refer [map-key]]
   [infix.macros :refer [from-string]]
   [seesaw.icon :refer [icon]]
   [clojure.java.io :as io]
   [clojure.string :as str])
  (:gen-class))

;; Recurso estatico (./resources/icon.png) va con el jar cuando se compila.
(def icon_path
  "Path al icono de la calculadora"
  (str (io/resource "icon.png")))

(defn ends-with-dot-zero?
  "Verifica si un numero termina con .0"
  [number]
  (str/ends-with? (str number) ".0"))

(defn parse-result
  "Devuelve un entero si es numero con decimal .0 o directamente un entero,
   caso contrario devuelve un flotante."
  [result]
  (let [r (str result)]
    (if-not (nil? (str/index-of r "."))
      (if (ends-with-dot-zero? r)
        (int result)
        (float result))
      (int result))))

(defn clear-visor
  "Limpia el visor de la calculadora"
  [visor-widget]
  (text! visor-widget "")
  (repaint! visor-widget))

(defn calculate-result
  "Funcion que calcula el resultado de una operacion"
  [visor-widget current-value]
  ;; from-string (de la libreria infix) parsea una cadena de texto como
  ;; "2+2" ejecuta la operacion y devuelve el valor como un string
  (text! visor-widget
         (str (try (parse-result (float ((from-string current-value))))
                   (catch Exception _err "Err"))))) ; x / 0 = Err

(defn button-action
  "Funcion que aplica un evento a un boton de la calculadora"
  [visor-widget current-value button-value]
  (let [aritmetic-operators ["+", "-", "*", "/"]]
    (if (= current-value "Err")
      (text! visor-widget "")
      (cond
        ;; Condicion 1
        ;; Si no hay nada en el visor no puede empezar con un operador
        (and (= current-value "")
             (some #(= button-value %) aritmetic-operators))
        (text! visor-widget  "")

        ;; Condicion 2
        ;; Si el ultimo caracter es un operador no se puede agregar otro
        ;; se reemplaza el operador por el nuevo
        (and (some #(str/ends-with? current-value %)
                   aritmetic-operators)
             (some #(= button-value %) aritmetic-operators))
        (text! visor-widget
               (str (subs current-value 0
                          (dec (count current-value))) button-value))
        ;; Default
        ;; Se escribe el nuevo valor
        :else
        (text! visor-widget (str current-value button-value))))))

(defn buttons-actions
  "Funcion que aplica un evento a todos los botones de la calculadora"
  [event]
  (let [visor-widget (select (to-root event) [:#visor]) ; El widget del visor
        current-value (text visor-widget) ; El valor actual del visor
        button-value (config event :text)] ; El valor del boton
    (case button-value
      "CE" (clear-visor visor-widget)
      "=" (calculate-result visor-widget current-value)
      ;; De aqui en adante lo que se hace es que si ya hay un operador al
      ;; final de la cadena se reemplaza si otro es presionado
      (button-action visor-widget current-value button-value))))

;; Widgets

(def visor-and-ce
  "Visor y boton de limpieza (CE)"
  (horizontal-panel
   :maximum-size [250 :by 50]
   :items [(text :id :visor :editable? false :font "Arial-18")
           (button :text "CE" :id "btn-ce" :font "Arial-18")]))

(def grid-buttons
  "Panel de botones"
  (grid-panel
   :columns 4
   :items [(button :text "1" :font "Arial-BOLD-14" :id "btn1")
           (button :text "2" :font "Arial-BOLD-14" :id "btn2")
           (button :text "3" :font "Arial-BOLD-14" :id "btn3")
           (button :text "+" :font "Arial-BOLD-14" :id "btn-plus")
           (button :text "4" :font "Arial-BOLD-14" :id "btn4")
           (button :text "5" :font "Arial-BOLD-14" :id "btn5")
           (button :text "6" :font "Arial-BOLD-14" :id "btn6")
           (button :text "-" :font "Arial-BOLD-14" :id "btn-subtract")
           (button :text "7" :font "Arial-BOLD-14" :id "btn7")
           (button :text "8" :font "Arial-BOLD-14" :id "btn8")
           (button :text "9" :font "Arial-BOLD-14" :id "btn9")
           (button :text "/" :font "Arial-BOLD-14" :id "btn-divide")
           (button :text "0" :font "Arial-BOLD-14" :id "btn0")
           (button :text "." :font "Arial-BOLD-14" :id "btn-decimal")
           (button :text "=" :font "Arial-BOLD-14" :id "btn-equals")
           (button :text "*" :font "Arial-BOLD-14" :id "btn-multiply")]))

(def calc-panel
  "Estructura general de la calculadora"
  (vertical-panel
   :id :calc-panel
   :items [visor-and-ce grid-buttons]))

(def calc-frame
  "Ventana principal de la calculadora"
  (frame :title "Calculadora",
         :content calc-panel ,
         :size [250 :by 250]
         :on-close :exit
         :resizable? false
         :icon (icon icon_path)))

;; Eventos

;; Se utiliza la funcion select, a cual se le pasa como paraemtro el elemento
;; raiz de la aplicacinon y el o los IDs de los widgets que se van a escuchar
;; Selector como CSS (# - ID, . - CLASS)
(listen (select calc-panel [:<javax.swing.JButton>])
        :action (fn [e] (buttons-actions e)))

;; Keybindings de teclas
(map-key calc-panel "NUMPAD0" (select calc-panel [:#btn0]))
(map-key calc-panel "NUMPAD1" (select calc-panel [:#btn1]))
(map-key calc-panel "NUMPAD2" (select calc-panel [:#btn2]))
(map-key calc-panel "NUMPAD3" (select calc-panel [:#btn3]))
(map-key calc-panel "NUMPAD4" (select calc-panel [:#btn4]))
(map-key calc-panel "NUMPAD5" (select calc-panel [:#btn5]))
(map-key calc-panel "NUMPAD6" (select calc-panel [:#btn6]))
(map-key calc-panel "NUMPAD7" (select calc-panel [:#btn7]))
(map-key calc-panel "NUMPAD8" (select calc-panel [:#btn8]))
(map-key calc-panel "NUMPAD9" (select calc-panel [:#btn9]))
(map-key calc-panel "ADD" (select calc-panel [:#btn-plus]))
(map-key calc-panel "SUBTRACT" (select calc-panel [:#btn-subtract]))
(map-key calc-panel "MULTIPLY" (select calc-panel [:#btn-multiply]))
(map-key calc-panel "DIVIDE" (select calc-panel [:#btn-divide]))
(map-key calc-panel "ENTER" (select calc-panel [:#btn-equals]))
(map-key calc-panel "DECIMAL" (select calc-panel [:#btn-decimal]))
;; ! TODO: este anda erratico, a veces si y a veces no. Arreglar luego.
(map-key calc-panel "BACK_SPACE" (select visor-and-ce [:#btn-ce]))

;; Funcion principal del programa

(defn -main
  "Funcion principal de la calculadora"
  [& _args]
  (native!)
  (invoke-later
   (-> calc-frame
       ;pack! ; Contrae todos los widgets a su minimo tamaño
       show!)))
