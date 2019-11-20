FROM rabbitmq:3.7.18-management
COPY ./elixir-1.8.2.ez /opt/rabbitmq/plugins/
COPY ./rabbitmq_message_deduplication-v3.8.x-0.4.3.ez /opt/rabbitmq/plugins/
RUN rabbitmq-plugins enable rabbitmq_message_deduplication