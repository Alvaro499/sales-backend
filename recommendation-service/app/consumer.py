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


    for message in consumer:
        order_data = message.value
        user_id = uuid.UUID(order_data['userId'])
        user_name = order_data['userName']
        products = order_data['products']


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
        except IntegrityError as e:
            session.rollback()
        except Exception as e:
            session.rollback()
        finally:
            session.close()