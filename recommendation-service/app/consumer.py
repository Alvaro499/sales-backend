from kafka import KafkaConsumer
import json

consumer = KafkaConsumer(
    'order_topic',
    bootstrap_servers='localhost:9092',
    auto_offset_reset='earliest',
    group_id='recommendation-service',
    enable_auto_commit=True,
    value_deserializer=lambda m: json.loads(m.decode('utf-8'))
)

print("Esperando mensajes de órdenes...")

for message in consumer:
    order_data = message.value
    user_id = order_data['userId']
    user_name = order_data['userName']
    products = order_data['products']

    print(f"🧾 Orden recibida del usuario {user_name} (ID: {user_id}):")
    for prod in products:
        print(f"🛒 Producto: {prod['name']} (ID: {prod['productId']})")

    # TODO: Guardar en DB
