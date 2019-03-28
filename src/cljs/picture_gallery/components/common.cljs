(ns picture-gallery.components.common)

(defn modal [header body footer]
  [:div.modal.is-active
   [:div.modal-background]
   [:div.modal-card
    [:header.modal-card-head [:h3 header]]
    [:section.modal-card-body body]
    [:div.modal-card-foot footer]]
   [:div {:style {:z-index 1}}]])

(defn input [type id placeholder fields]
  [:input.input
   {:type type
    :placeholder placeholder
    :value (id @fields)
    :on-change #(swap! fields assoc id (-> % .-target .-value))}])

(defn form-input [type label id placeholder fields optional?]
  [:div.field
   [:label.label label]
   (if optional?
     [input type id placeholder fields]
     [:div.control.has-icons-right
      [input type id placeholder fields]
      [:span.icon.is-right "*"
       #_[:i.fas.fa-asterisk]]])])

(defn text-input [label id placeholder fields & [optional?]]
  (form-input :text label id placeholder fields optional?))

(defn password-input [label id placeholder fields & [optional?]]
  (form-input :password label id placeholder fields optional?))
