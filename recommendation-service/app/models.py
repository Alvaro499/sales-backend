import uuid
import datetime
from sqlalchemy import Column, String, ForeignKey, DateTime
from sqlalchemy.orm import relationship, declarative_base
from sqlalchemy.dialects.postgresql import UUID  # Solo para PostgreSQL

Base = declarative_base()

class Product(Base):
    __tablename__ = "products"
    product_id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4, index=True)
    name = Column(String, nullable=False)
    description = Column(String)

class User(Base):
    __tablename__ = "users"
    user_id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4, index=True)
    name = Column(String, nullable=False)

class Order(Base):
    __tablename__ = "orders"
    order_id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4, index=True)
    user_id = Column(UUID(as_uuid=True), ForeignKey("users.user_id"))
    date = Column(DateTime, default=datetime.datetime.utcnow)

    user = relationship("User", back_populates="orders")
    lines = relationship("OrderLine", back_populates="order")

User.orders = relationship("Order", back_populates="user")

class OrderLine(Base):
    __tablename__ = "order_lines"
    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4, index=True)
    order_id = Column(UUID(as_uuid=True), ForeignKey("orders.order_id"))
    product_id = Column(UUID(as_uuid=True), ForeignKey("products.product_id"))

    order = relationship("Order", back_populates="lines")
    product = relationship("Product")
