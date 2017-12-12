variable "key_name" {}
#variable "public_key_path" {}

provider "aws" {
  region     = "eu-central-1"
}

# Create a VPC to launch our instances into
resource "aws_vpc" "default" {
  cidr_block = "10.0.0.0/16"
}

# Create an internet gateway to give our subnet access to the outside world
resource "aws_internet_gateway" "default" {
  vpc_id = "${aws_vpc.default.id}"
}

# Grant the VPC internet access on its main route table
resource "aws_route" "internet_access" {
  route_table_id         = "${aws_vpc.default.main_route_table_id}"
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = "${aws_internet_gateway.default.id}"
}

# TODO remove public ip map
# Create a subnet to launch our instances into
resource "aws_subnet" "default" {
  vpc_id                  = "${aws_vpc.default.id}"
  cidr_block              = "10.0.1.0/24"
  map_public_ip_on_launch = true
}

# A security group for the ELB so it is accessible via the web
resource "aws_security_group" "elb" {
  name        = "terraform_example_elb"
  description = "Used in the terraform"
  vpc_id      = "${aws_vpc.default.id}"

  # HTTP access from anywhere
  ingress {
    from_port   = 9000
    to_port     = 9000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # outbound internet access
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Our default security group to access
# the instances over SSH and HTTP
resource "aws_security_group" "default" {
  name        = "terraform_example"
  description = "Used in the terraform"
  vpc_id      = "${aws_vpc.default.id}"

  # SSH access from anywhere
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # web access from anywhere
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # outbound internet access
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}


# load balancer
resource "aws_elb" "web" {
  name = "terraform-example-elb"

  subnets         = ["${aws_subnet.default.id}"]
  security_groups = ["${aws_security_group.elb.id}"]
  instances       = ["${aws_instance.worker-node.id}"]

  listener {
    instance_port     = 80
    instance_protocol = "http"
    lb_port           = 9000
    lb_protocol       = "http"
  }
}


resource "aws_instance" "worker-node" {
  connection {
    user = "centos"
    private_key = "${file("${path.module}/siemens-app.pem")}"
  }

  ami = "ami-a90a82c6" #ami for the invoker

  instance_type = "t2.micro"

  key_name = "${var.key_name}"
  #key_name = "siemens-app"

  vpc_security_group_ids = ["${aws_security_group.default.id}"]

  subnet_id = "${aws_subnet.default.id}"

  provisioner "remote-exec" {
      inline = [
        "sudo sh -c 'echo \"10.0.1.10 dispatcher\" >> /etc/hosts'",
        "sudo sh -c 'echo \"10.0.1.11 maven-cache\" >> /etc/hosts'",
      ]
    }

}

resource "aws_instance" "dispatcher-node" {
  connection {
    user = "centos"
    private_key = "${file("${path.module}/siemens-app.pem")}"
  }

  ami = "ami-ad0d85c2" #ami for the dispatcher

  instance_type = "t2.micro"

  key_name = "${var.key_name}"

  vpc_security_group_ids = ["${aws_security_group.default.id}"]

  subnet_id = "${aws_subnet.default.id}"

  private_ip = "10.0.1.10"

}

resource "aws_instance" "nexus-node" {
  connection {
    user = "centos"
    private_key = "${file("${path.module}/siemens-app.pem")}"
  }

  ami = "ami-c375fdac" #ami for the dispatcher

  instance_type = "t2.micro"

  key_name = "${var.key_name}"

  vpc_security_group_ids = ["${aws_security_group.default.id}"]

  subnet_id = "${aws_subnet.default.id}"

  private_ip = "10.0.1.11"

}

# ami-7f52da10
