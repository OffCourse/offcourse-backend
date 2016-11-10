(ns app.command.messages)

(def errors
  {:invalid-user "profile and token do not belong to the same user"
   :not-repository "the course you try to save is not in this repository, do you want to fork it instead?"
   :not-signed-up "you have not signed up to offcourse yet."
   :not-signed-in "you need to be signed-in to perform this action"
   :not-curator "you are not the curator of the course that you try to save, do you want to fork it instead?"})
