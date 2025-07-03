from kafka import KafkaConsumer
import json
import uuid
from sqlalchemy.exc import IntegrityError
from app.database import SessionLocal
from app.models import User, Product, Order, OrderLine

def run_consumer():
    consumer = KafkaConsumer(
        'order_topic4',
        bootstrap_servers='localhost:9092',
        auto_offset_reset='earliest',
        group_id='recommendation-service',
        enable_auto_commit=True,
        value_deserializer=lambda m: json.loads(m.decode('utf-8'))
    )

    print("Esperando mensajes de órdenes...")

    for message in consumer:
        order_data = message.value
        user_id = uuid.UUID(order_data['userId'])
        user_name = order_data['userName']
        products = order_data['products']

        print(f"Orden recibida del usuario {user_name} (ID: {user_id}):")
        for prod in products:
            print(f"Producto: {prod['name']} (ID: {prod['productId']})")

        session = SessionLocal()
        try:
            user = session.query(User).filter_by(user_id=user_id).first()
            if not user:
                user = User(user_id=user_id, name=user_name)
                session.add(user)
                session.flush()

            order = Order(user_id=user.user_id)
            session.add(order)
            session.flush()

            for p in products:
                product_id = uuid.UUID(p['productId'])
                product_name = p['name']
                product_desc = p.get('description', '')

                product = session.query(Product).filter_by(product_id=product_id).first()
                if not product:
                    product = Product(product_id=product_id, name=product_name, description=product_desc)
                    session.add(product)
                    session.flush()

                order_line = OrderLine(order_id=order.order_id, product_id=product.product_id)
                session.add(order_line)

            session.commit()
            print("Orden guardada correctamente.\n")
        except IntegrityError as e:
            session.rollback()
            print("Error de integridad al guardar la orden:", e)
        except Exception as e:
            session.rollback()
            print("Error al guardar en la base de datos:", e)
        finally:
            session.close()