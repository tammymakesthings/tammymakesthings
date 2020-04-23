{
  :title  "rotor_machine: Simple Enigma Simulation in Ruby"
  :layout :page
}

![German Enigma machine](/img/rotor_machine/Bundesarchiv_Enigma.jpg)

The `RotorMachine` gem provides a simple Ruby implementation of the
[Enigma](https://en.wikipedia.org/wiki/Enigma_machine) rotor encryption machine.

I wrote RotorMachine primarily as an exercise in Test-Driven Development with
RSpec. It is not intended to be efficient or performant, and I wasn't striving much
for idiomatic conciseness. My aims were fairly modular code and a relatively
complete RSpec test suite.

Many thanks to Kevin Sylvestre, whose
[blog post](https://ksylvest.com/posts/2015-01-03/the-enigma-machine-using-ruby)
helped me understand some aspects of the internal workings of the Enigma and
how the signals flowed through the pieces of the machine.

## Installation

Add this line to your application's Gemfile:

```ruby
gem 'rotor_machine'
```

And then execute:

    $ bundle

Or install it yourself as:

    $ gem install rotor_machine

## Architecture

The [`RotorMachine::Machine`](http://www.rubydoc.info/github/tammymakesthings/rotor_machine/master/RotorMachine/Machine)
class serves as the entrypoint and orchestrator for an Enigma machine.

### Components of an Enigma machine

The Enigma machine, as represented by the [RotorMachine](http://www.rubydoc.info/github/tammymakesthings/rotor_machine/master)
module, consists of the following components:

* One or more [rotors](http://www.rubydoc.info/github/tammymakesthings/rotor_machine/master/RotorMachine/Rotor), which
  perform the transposition ciphering and also rotate to produce a polyalphabetic (rather
  than simple substitution) cipher.

* A [reflector](http://www.rubydoc.info/github/tammymakesthings/rotor_machine/master/RotorMachine/Reflector), which
  performs a simple symmetric substitution of letters

* A [plugboard](http://www.rubydoc.info/github/tammymakesthings/rotor_machine/master/RotorMachine/Plugboard), which
  allows pairs of letters to be transposed on a per-message basis.

On an actual Enigma machine, these components are all electromechanical, and
the Enigma also included a keyboard, a grid of lights to show the results, and
in some cases a printer. Since this is a simulated Enigma, obviously, no
keyboard/printer are supplied here. In this simulation, the
[Machine](http://www.rubydoc.info/github/tammymakesthings/rotor_machine/master/RotorMachine/Machine)
class serves to encapsulate all of these components.

The polyalphabetic encryption of the Enigma comes from the fact that the
rotors are linked (mechanically in a real Enigma) so that they rotate
one or more "steps" after each character, changing the signal paths and
transpositions. This means that a sequence of the same plaintext character
will encipher to different ciphertext characters.

The rotors are designed to advance such that each time a rotor completes
a full revolution, it will advance the rotor to its left once. The rotors
allow you to configure how many positions they advance when they do. So,
assuming all rotors are advancing one position at a time, if the rotors
have position "AAZ", their state after the next character is typed will
be "ABA".

To learn much more about the inner workings of actual Enigma machines,
visit [Enigma Machine (Wikipedia)](https://en.wikipedia.org/wiki/Enigma_machine).

###  The Signal Path of Letters

Here's a visual depiction of the signal path of a single character through
a (physical) Enigma machine:

![Enigma signal path](/img/rotor_machine/File-Enigma_wiring_kleur.png)

As you can see, the electrical signal from a keypress is routed through the
plugboard, then through each of the rotors in sequence from left to right.
The signal then passes through the reflector (where it is transposed again),
then back through the rotors in reverse order, and finally back through the
plugboard a second time before being displayed on the light grid and/or
printer.

The result of the machine's signal path being a loop is that encryption and
decryption are the same operation. That is to say, if you set the rotors
and plugboard, and then type your plaintext into the machine, you'll get
a string of ciphertext. If you then reset the machine to its initial state
and type the ciphertext characters into the machine, you'll produce your
original plaintext.

One consequence of the Enigma's design is that a plaintext letter will never
encipher to itself. The Allies were able to exploit this property to help
[break the Enigma's encryption](https://en.wikipedia.org/wiki/Cryptanalysis_of_the_Enigma)
during World War II.

## Usage

To use the RotorMachine Enigma machine, you need to perform the following
steps:

1. Create a new `RotorMachine::Machine` object.
2. Add one or more `RotorMachine::Rotor`s  to the `rotors` array.
3. Set the `reflector` to an instance of the `RotorMachine::Reflector` class.
4. Make any desired connections in the Plugboard.
5. Optionally, set the rotor positions with `#set_rotors`.

You're now ready to encipher and decipher your text using the `#encipher`
method to encode/decode, and `#set_rotors` to reset the machine state.

The `#default_machine` and `#empty_machine` class methods are shortcut
factory methods whcih set up, respectively, a fully configured machine
with a default set of rotors and reflector, and an empty machine with
no rotors or reflector.

You can also create a new `RotorMachine::Machine` (or its various parts)
using the `RotorMachine::Factory` methods, as shown in the second example.

## Example

```ruby
require 'rotor_machine'

machine = RotorMachine::Machine.empty_machine

machine.rotors << RotorMachine::Rotor.new(RotorMachine::Rotor::ROTOR_I, "A", 1)
machine.rotors << RotorMachine::Rotor.new(RotorMachine::Rotor::ROTOR_II, "A", 1)
machine.rotors << RotorMachine::Rotor.new(RotorMachine::Rotor::ROTOR_III, "A", 1)
machine.reflector = RotorMachine::Reflector.new(RotorMachine::Reflector::REFLECTOR_A)

machine.plugboard.connect("A", "M")
machine.plugboard.connect("Q", "K")

machine.set_rotors("CFL")
plaintext = "This is a super secret message".upcase
ciphertext = machine.encipher(plaintext)      # => "MYGMS ZLTWS AAIDD VTGOC RFKFO"

machine.set_rotors("CFL")
new_plaintext = machine.encipher(ciphertext)  # => "THISI SASUP ERSEC RETME SSAGE"
```

## Example - Simplified Setup Using the Factory

```ruby
require 'rotor_machine'

machine = RotorMachine::Factory.build_machine(
  rotors: [:ROTOR_I, :ROTOR_II, :ROTOR_III],
  reflector: :REFLECTOR_A,
  connections: {"A" => "M", "Q" => "K" }
)
machine.set_rotors("CFL")

plaintext = "This is a super secret message".upcase
ciphertext = machine.encipher(plaintext)      # => "MYGMS ZLTWS AAIDD VTGOC RFKFO"

machine.set_rotors("CFL")
new_plaintext = machine.encipher(ciphertext)  # => "THISI SASUP ERSEC RETME SSAGE"
```

## Using the Wrapper DSL

A simple wrapper DSL (domain-specific language) is provided, primarily for
testing and other "conversational" or interactive uses. This DSL is defined in
the `RotorMachine::Session` class. Usage is similar to the following:

```ruby
RotorMachine.Session do
  default_machine

  set_rotors "AAA"
  connect "A", "G"
  encipher "THIS IS A SUPER SECRET MESSAGE"
  ct = last_result

  set_rotors "AAA"
  encipher ct
  puts last_result    # THISI SASUP ERSEC RETME SSAGE
end
```

After the operations in the block are executed, the `RotorMachine.Session` method
will return the `RotorMachine::Session` object, which can be further reused if
needed.

## Using the `rotor_machine` REPL

The `rotor_machine` executable instantatiates an instance of the
`RotorMachine::Shell` class and then runs its `repl()` method. This creates an
interactive shell whereby you can interact with a rotor machine. The
`RotorMachine::Shell` is an interactive wrapper around `RotorMachine::Session`
with help, readline and ANSI colorization added.

Either run the `rotor_machine` executable, or run the following code to instantiate
a REPL:

    `RotorMachine::Shell.new().repl`

The REPL provides interactive usage help. Type `help` at the REPL prompt for more
details.

## Documentation

The classes in
[`lib/rotor_machine/`](https://github.com/tammymakesthings/rotor_machine/tree/master/lib/rotor_machine)
all contain [documentation](http://www.rubydoc.info/github/tammymakesthings/rotor_machine/master/) that
pretty exhaustively describe their operation.
The RSpec tests in the [`spec/`](https://github.com/tammymakesthings/rotor_machine/tree/master/spec)
directory are also instructive for how the library works and how to use it.

## Development

After checking out the repo, run `bin/setup` to install dependencies. Then,
run `rake spec` to run the tests. You can also run `bin/console` for an
interactive prompt that will allow you to experiment.

To install this gem onto your local machine, run `bundle exec rake install`.

To release a new version, update the version number in `version.rb`, and then
run `bundle exec rake release`, which will create a git tag for the version,
push git commits and tags, and push the `.gem` file to [rubygems.org](https://rubygems.org).

This gem depends on the [`tcravit_ruby_lib`](https://github.com/tammymakesthings/tcravit_ruby_lib)
gem, which provides Rake tasks to update the version number. You can use the
`bundle exec rake version:bump:build`, `bundle exec version:bump:minor` and
`bundle exec rake version:bump:major` tasks to increment the parts of
the version number. (These tasks rewrite the file
[`lib/rotor_machine/version.rb`](https://github.com/tammymakesthings/rotor_machine/blob/master/lib/rotor_machine/version.rb).
After using them, you'll need to run a `git add lib/rotor_machine/version.rb`
and `git commit -m "version bump"`.

## License

The gem is available as open source under the terms of the
[Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0) license.

## Image Credits

* Enigma image - from [Wikimedia Commons](https://commons.wikimedia.org/wiki/File:Bundesarchiv_Bild_183-2007-0705-502,_Chiffriermaschine_%22Enigma%22.jpg),
  provided by Das Bundesarchiv (German Federal Archives).

* Enigma signal path image - from [Wikimedia Commons](https://commons.wikimedia.org/wiki/File:Enigma_wiring_kleur.svg),
  by [MesserWoland](https://commons.wikimedia.org/wiki/User:MesserWoland)

