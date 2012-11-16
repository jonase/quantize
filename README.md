# quantize

A Leiningen plugin for [codeq](https://github.com/Datomic/codeq).

## Usage

Put `[lein-quantize "0.1.0-SNAPSHOT"]` into the `:plugins` vector of your
`:user` profile.

Go to a local git checkout of a Clojure program you want to
analyze. You can run quantize in memory without a running
transactor. The following commands are currently available

    $ lein quantize :authors
    $ lein quantize :initial-commit
    $ lein quantize :commit-count
    $ lein quantize :first-defined \"ns.qualified/fn-name\"
    $ lein quantize :q "[:find ?email :where [_ :email/address ?email]]"

If you run quantize without a transactor you'll end up analyzing your
git history on every single run. Instead you can start up a transactor
(see [the datomic docs](http://datomic.com)) and add

    {:quantize {:storage "free"}}

to your project.clj. Other options are

    :port (default 4334)
    :host (default localhost)
    :name (defaults to your projects name)

If you use the same `:name` for several projects you can run queries
across multiple projects at once.

## TODO
* More queries (pull requests welcome!)

## License

Copyright © 2012 Jonas Enlund

Distributed under the Eclipse Public License, the same as Clojure.
