# UDP_Selective_Repeat
An implementation of a reliable UDP using selective repeat to send/receive an image file on localhost.

## Introduction
This is meant to be an excercise to better understand reliable communiction methods such as selective repeat. The Folder "/UDP Selective Repeat/Sender Side" contains java 1.8 scripts for sending an image file that can be specified via command line (See usage). The image is broken down into chunks of 1kB and each chunk is placed into a packet and sent. The simulated network is lossy and can be configured to have more/less noise using command line prompts (See usage).

The folder "/UDP Selective Repeat/Receiver Side" contains python script for receiving the sent packets using similar  implementations. Upon complete reception of the image sent by Sender.java, the image will be saved to the the same directory as 'received.png'

## Multithreading
The Sender implementation comprises of a separate class "myThread" that extends the Thread class. The class is written such that each myThread object created is reponsible for the delivery of one packet.

## Usage- Sender
Open a command prompt within "/UDP Selective Repeat/Sender Side" and enter the following command
```ruby
java Sender image.png 60000 10 10
```

This will start the Sender Side and must be run before the Reciever Side. The arguments above represent the following:

java Sender <file_path> <receiver_port> <window_size_N> <retransmission_timeout>

where:
<file_path> may be substituted for any image you want to send- directory to which may be specified if it lies outside the current working directory.
<receiver_port> is the port that the Reciever binds to and must be the same at the reciver side.
<window_size_N> is the number of threads( or more appropriately, the number of un-ACKed packets that can be en-route ) that are allowed to be running simultaneously at a given time.
<retransmission_timeout> is the timer in seconds within which each thread expects an ACK for its packet, after which it will resend the packet and wait for an ACK again.

Note that since this is a simulation within localhost, paclets are dropped or'lost' to mimic real networks.

## Usage- Receiver
Open a command prompt within "/UDP Selective Repeat/Sender Side" and enter the following command

```ruby
python receiver.py 60000 10 0 5
```

This will start the Receiver Side and must be run before the Reciever Side. The arguments above represent the following:

python receiver.py <bind_port> <window_size_N> <packet_loss_probability_p> <max_packet_delay_Dmax>

where:
<bind_port> is the port number onto which the local host reciver binds to.
<window_size_N> is the is the number of the number of packets in the sliding window that are expected at a time.
<packet_loss_probability_p> is the probability that a successflly recived packet will be dropped- this is to simultate a lossy network.
<max_packet_delay_Dmax> is used to randomly select a time delay between [0.Dmax] seconds to simulate a packets travelling across different distances.

## END
Hope you all find it useful and helpful in yor quest to understand reliable communication protocols.
